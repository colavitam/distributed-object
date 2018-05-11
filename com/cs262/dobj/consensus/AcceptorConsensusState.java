package com.cs262.dobj.consensus;

import java.util.HashMap;
import java.io.Serializable;

class AcceptorConsensusState<ObjType extends Serializable> extends ConsensusState<ObjType> {
  private HashMap<Long, PhaseInfo> proposals; // current state of various proposals
  private long earliestUnused; // earliest round for which no phase 2 has gone out

  public AcceptorConsensusState(ObjType inst) {
    super(inst);
    proposals = new HashMap<Long, PhaseInfo>();
    earliestUnused = getCheckpoint();
  }

  public AcceptorConsensusState(ConsensusState state) {
    super(state);
    proposals = new HashMap<Long, PhaseInfo>();
    earliestUnused = getCheckpoint();
  }

  public AcceptorConsensusState(AcceptorConsensusState state) {
    super(state);
    proposals = state.proposals;
    earliestUnused = state.earliestUnused;
  }

  protected void processRequest(long src, RequestMessage m) {
    // reply to inform requester who leader is
    ctx.sendMessage(src, new LeaderMessage(getLeader()));
  }

  protected void processPrepare(long src, PrepareMessage m) {
    // reply with promise if higher proposal number
    // otherwise send abort message
    long seqNum = m.seqNum;
    long propNum = m.propNum;

    PhaseInfo phase = proposals.getOrDefault(seqNum, PhaseInfo.MIN_PHASE);
    if (phase.precedes(src, propNum)) {
      // send promise
      proposals.put(seqNum, new PhaseInfo(seqNum, propNum, phase));
      ctx.sendMessage(src, new PromiseMessage(seqNum, propNum, earliestUnused, phase));
    } else {
      // send abort message
      ctx.sendMessage(src, new AbortPrepareMessage(seqNum, propNum, phase));
    }
  }
  
  protected void processAccept(long src, AcceptMessage m) {
    // reply with accepted if appropriate proposal number
    // otherwise send abort message
    long seqNum = m.seqNum;
    long propNum = m.propNum;

    PhaseInfo phase = proposals.getOrDefault(seqNum, null);
    if (phase == null) {
      // unexpected
      return;
    }

    // mark this round as used
    if (earliestUnused < seqNum + 1) {
      earliestUnused = seqNum + 1;
    }

    // ensure same operation as before, if there was one
    if (phase.src == src && phase.propNum == propNum && m.op.isSame(phase.accepted)) {
      // send accepted
      setLeader(src); // accept acknowledges leadership
      proposals.put(seqNum, new PhaseInfo(src, propNum, m.op));
      ctx.sendMessage(src, new AcceptedMessage(seqNum, m.op, phase));
    } else {
      // send abort message
      ctx.sendMessage(src, new AbortAcceptMessage(seqNum, propNum, phase));
    }
  }
}
