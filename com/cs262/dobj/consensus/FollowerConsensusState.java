package com.cs262.dobj.consensus;

import java.util.HashMap;
import java.io.Serializable;

import com.cs262.dobj.channel.*;

class FollowerConsensusState<ObjType extends Serializable> extends ConsensusState<ObjType> {
  private static class PhaseInfo {
    public final long src;
    public final long propNum;
    public final boolean hasAccepted;
    public final Operation accepted;

    public PhaseInfo(long src, long propNum) {
      this.src = src;
      this.propNum = propNum;
      this.hasAccepted = false;
      this.accepted = null;
    }

    public PhaseInfo(long src, long propNum, Operation op) {
      this.src = src;
      this.propNum = propNum;
      this.hasAccepted = true;
      this.accepted = op;
    }

    public PhaseInfo(long src, long propNum, PhaseInfo prev) {
      this.src = src;
      this.propNum = propNum;
      this.hasAccepted = prev.hasAccepted;
      this.accepted = prev.accepted;
    }

    public boolean precedes(long src, long propNum) {
      return this.src > src || this.propNum < propNum;
    }
  }
  private HashMap<Long, PhaseInfo> proposals; // current state of various proposals

  public FollowerConsensusState(ObjType inst) {
    super(inst);
    proposals = new HashMap<Long, PhaseInfo>();
  }

  public FollowerConsensusState(ConsensusState state) {
    super(state);
    proposals = new HashMap<Long, PhaseInfo>();
  }

  protected void processRequest(ConsensusContext<ObjType> ctx,
                                DistributedChannel<?, ConsensusMessage> channel,
                                long src, RequestMessage m) {
    // reply to inform requester who leader is
    // TODO
  }

  protected void processPrepare(ConsensusContext<ObjType> ctx,
                                DistributedChannel<?, ConsensusMessage> channel,
                                long src, PrepareMessage m) {
    // reply with promise if higher proposal number
    // otherwise send abort message
    long seqNum = m.seqNum;
    long propNum = m.propNum;

    PhaseInfo phase = proposals.getOrDefault(seqNum, new PhaseInfo(Long.MAX_VALUE, -1));
    if (phase.precedes(src, propNum)) { // send promise
      proposals.put(seqNum, new PhaseInfo(seqNum, propNum, phase));
      PromiseMessage pm;
      if (phase.hasAccepted) {
        pm = new PromiseMessage(seqNum, phase.propNum, phase.accepted);
      } else {
        pm = new PromiseMessage(seqNum, phase.propNum);
      }
      channel.sendMessage(src, pm);
    } else { // send abort message
      channel.sendMessage(src, new AbortPrepareMessage(seqNum, propNum));
    }
  }
  
  protected void processPromise(ConsensusContext<ObjType> ctx,
                                DistributedChannel<?, ConsensusMessage> channel,
                                long src, PromiseMessage m) {
    // we don't expect to receive this since we're not the leader
    // ignore
  }

  protected void processAccept(ConsensusContext<ObjType> ctx,
                               DistributedChannel<?, ConsensusMessage> channel,
                               long src, AcceptMessage m) {
    // reply with accepted if appropriate proposal number
    // otherwise send abort message
    long seqNum = m.seqNum;
    long propNum = m.propNum;

    PhaseInfo phase = proposals.getOrDefault(seqNum, null);
    if (phase == null || (phase.src == src && phase.propNum == propNum)) { // send accepted
      // TODO ensure same operation as before, if there was one
      proposals.put(seqNum, new PhaseInfo(src, propNum, m.op));
      channel.sendMessage(src, new AcceptedMessage(seqNum, propNum));
    } else { // send abort message
      channel.sendMessage(src, new AbortAcceptMessage(seqNum, propNum));
    }
  }

  protected void processAccepted(ConsensusContext<ObjType> ctx,
                                 DistributedChannel<?, ConsensusMessage> channel,
                                 long src, AcceptedMessage m) {
    // we don't expect to receive this since we're not the leader
    // ignore
  }
}
