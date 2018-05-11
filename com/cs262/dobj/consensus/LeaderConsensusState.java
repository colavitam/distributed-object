package com.cs262.dobj.consensus;

import java.util.*;
import java.util.Map.Entry;
import java.io.Serializable;

// TODO refactor for coherency
// this violates tell-don't-ask everywhere
// and RoundInfo is being used as some terrible footstool class
// there are getters and getter-setters and they're all mixed up for the same instance vars
// it makes no sense and I'm sorry I factored it this way
class LeaderConsensusState<ObjType extends Serializable> extends AcceptorConsensusState<ObjType> {
  private class RoundInfo {
    private HashMap<Long, PhaseInfo> participants; // latest acceptor states we elicited
    private HashSet<Long> accepted; // who has accepted so far?
    private long propNum; // highest known sent proposal number this round
    private Operation op; // operation we're attempting to pass this round

    public RoundInfo(HashMap<Long, PhaseInfo> participants, long propNum, Operation op) {
      this.participants = participants;
      this.accepted = new HashSet<>();
      this.propNum = propNum;
      this.op = op;
    }

    public Set<Long> getParticipants() {
      return participants.keySet();
    }

    public long getPropNum() {
      return propNum;
    }

    // aborts this proposal
    public long nextProp() {
      this.accepted = new HashSet<>();
      return ++propNum;
    }

    // aborts this proposal
    public void advanceProp(long propNum) {
      this.accepted = new HashSet<>();
      if (this.propNum < propNum) {
        this.propNum = propNum;
      }
    }

    // does this round's operation conflict with another?
    // i.e. is this round's operation the same as this other one's?
    public boolean opConflicts(Operation op) {
      if (this.op == null || op == null) {
        return false;
      }
      return !this.op.isSame(op);
    }

    // this round will give up trying its current operation
    public Operation hoistOp() {
      Operation op = this.op;
      this.op = null;
      return op;
    }

    public Operation getOp() {
      return this.op;
    }

    public boolean updateParticipant(long src, PhaseInfo phase) {
      PhaseInfo cur = participants.get(src);
      if (cur == null) {
        // unexpected
        return false;
      }

      // expect phase.propNum <= propNum

      // if we've heard later news, forget it
      if (cur.precedes(phase)) {
        participants.put(src, phase);
        return true;
      }
      return false;
    }

    // return: null if no quorum accepted yet
    // or the accepting quorum, if a quorum of participants has accepted
    public Operation participantAccepted(long src) {
      PhaseInfo phase = participants.get(src);
      if (phase == null) {
        // unexpected
        return null;
      }

      // check it's the same operation they've all accepted...
      // it's a bug otherwise
      for (long part : accepted) {
        PhaseInfo p = participants.get(src);
        if (p == null) {
          // unexpected
          return null;
        }
        if (!phase.accepted.isSame(p.accepted)) {
          // unexpected
          return null;
          // TODO and honestly a lot of the time we should be starting new rounds here
        }
      }

      accepted.add(src);
      if (accepted.size() * 2 > participants.size()) {
        return phase.accepted;
      } else {
        return null;
      }
    }

    // returns: a quorum of participants that has given a promise at this propNum
    // or null if no such quorum exists
    public Set<Long> promisedQuorum() {
      HashSet<Long> quorum = new HashSet<>();
      for (Entry<Long, PhaseInfo> part : participants.entrySet()) {
        PhaseInfo phase = part.getValue();
        if (phase.src == ctx.getId() && phase.propNum == propNum) {
          quorum.add(part.getKey());
        }
      }
      
      if (quorum.size() * 2 > participants.size()) {
        return quorum;
      } else {
        return null;
      }
    }

    // returns: highest-numbered accepted operation
    public Operation highestAccepted(Set<Long> parts) {
      PhaseInfo highestPhase = PhaseInfo.MIN_PHASE;
      Operation op = null;

      for (long part : parts) {
        PhaseInfo phase = participants.get(part);
        if (phase != null && highestPhase.precedes(phase) && phase.accepted != null) {
          op = phase.accepted;
        }
      }

      return op;
    }
  }
  private HashMap<Long, RoundInfo> rounds;
  // invariants...
  // - phase 1 has been attempted on all rounds in HashMap
  // - no round has been attempted >= earliestFresh

  private long earliestFresh; // known no free rounds before here
  // to an acceptor, earliestUnused is the earliest round no phase 2 has occurred on
  // the proposer cares about the earliest round they haven't tried phase 1 on
  // we need not do this on rounds for which acceptors have experienced only phase 1
  // they will tell us and we will re-issue prepare

  public LeaderConsensusState(ObjType inst) {
    super(inst);
    rounds = new HashMap<Long, RoundInfo>();
    earliestFresh = getCheckpoint();
  }

  public LeaderConsensusState(ConsensusState<ObjType> state) {
    super(state);
    rounds = new HashMap<Long, RoundInfo>();
    earliestFresh = getCheckpoint();
  }

  public synchronized void setContext(ConsensusContext<ObjType> ctx) {
    super.setContext(ctx);
    setLeader(ctx.getId()); // grab leadership
  }

  // got a request; execute prepare broadcast for paxos
  // TODO start client heartbeat thread
  // TODO start timeout thread
  protected void processRequest(long src, RequestMessage m) {
    paxosPhase1(earliestFresh++, m.op);
  }

  protected void processAbortPrepare(long src, AbortPrepareMessage m) {
    commonAbort(src, m.seqNum, m.propNum, m.phase);
  }

  // register promises from nodes
  protected void processPromise(long src, PromiseMessage m) {
    long seqNum = m.seqNum;
    long propNum = m.propNum;
    long earliestUnused = m.earliestUnused;
    PhaseInfo phase = m.phase;

    RoundInfo round = rounds.get(seqNum);
    if (round == null) {
      // unexpected
      return;
    }

    // update acceptor state
    round.updateParticipant(src, new PhaseInfo(ctx.getId(), propNum, phase));

    // checkpointing for leader switch...
    if (earliestFresh < earliestUnused) {
      // rounds have happened that we don't know about
      // attempt phase 1 on rounds we haven't tried yet, using no-op
      for (long seq = earliestFresh; seq < earliestUnused; seq++) {
        paxosPhase1(seq, new NoOperation(ctx.getId(), ctx.getFreshOpNum()));
      }
      earliestFresh = earliestUnused;
    }

    // out of date? shortcut
    if (propNum != round.getPropNum()) {
      return;
    }

    // begin phase 2 when enough promises received
    Set<Long> quorum = round.promisedQuorum();
    if (quorum != null) {
      Operation op = round.highestAccepted(quorum);
      if (op != null) {
        if (round.opConflicts(op)) {
          // they've accepted something else
          // hoist our operation, if any, to a new round
          // operation might duplicate if we sent out an accept before aborting
          // so we ensure it's different above
          Operation prevOp = round.hoistOp();
          if (!(prevOp instanceof NoOperation)) {
            // but don't hoist NoOperation because that's just for
            // finding out where the frontier is
            paxosPhase1(earliestFresh++, prevOp);
          }
        }

        // use highest-numbered accepted proposal
        paxosPhase2(seqNum, quorum, op);
      } else {
        // use our own proposal
        // round.op should be != null since if phase.accepted != null then
        // round.hasPromised() should also != null
        paxosPhase2(seqNum, quorum, round.getOp());
      }
    }
  }

  protected void processAbortAccept(long src, AbortAcceptMessage m) {
    // someone else is trying to get a quorum!
    // (we got a quorum of promises, which has since been disrupted)
    if (m.phase.src > ctx.getId()) {
      // if they're bigger than us, abdicate leadership
      setLeader(m.phase.src);
      ctx.setState(new AcceptorConsensusState(this));
      // TODO make sure this goes to the right constructor
    } else {
      commonAbort(src, m.seqNum, m.propNum, m.phase);
    }
  }

  protected void processAccepted(long src, AcceptedMessage m) {
    // register acceptance from node
    long seqNum = m.seqNum;
    PhaseInfo phase = m.phase;

    RoundInfo round = rounds.get(seqNum);
    if (round == null) {
      // unexpected
      return;
    }

    round.updateParticipant(src, new PhaseInfo(ctx.getId(), phase.propNum, m.op));

    // out of date? shortcut
    if (phase.propNum != round.getPropNum()) {
      return;
    }

    Operation op = round.participantAccepted(src);
    if (op != null) {
      for (long part : super.getParticipants()) {
        ctx.sendMessage(part, new ChosenMessage(seqNum, op));
      }
    }
  }

  // aborts this proposal
  private void castPrepare(long seqNum) {
    RoundInfo round = rounds.get(seqNum);
    long propNum = round.nextProp();
    for (long part : round.getParticipants()) {
      ctx.sendMessage(part, new PrepareMessage(seqNum, propNum));
    }
  }

  // start phase 1 on a previously unused round
  // TODO start timeout
  private void paxosPhase1(long seqNum, Operation op) {
    Set<Long> partIDs = super.getParticipants(seqNum);

    // set up round info
    HashMap<Long, PhaseInfo> participants = new HashMap<>();
    for (Long id : partIDs) {
      participants.put(id, PhaseInfo.MIN_PHASE);
    }
    rounds.put(seqNum, new RoundInfo(participants, -1, op));

    // phase 1 leader broadcast
    castPrepare(seqNum);
  }

  // TODO start timeout
  private void paxosPhase2(long seqNum, Set<Long> quorum, Operation op) {
    RoundInfo round = rounds.get(seqNum);
    long propNum = round.getPropNum();
    for (long part : quorum) {
      ctx.sendMessage(part, new AcceptMessage(seqNum, propNum, op));
    }
  }

  // restart with prepare at higher proposal number
  private void commonAbort(long src, long seqNum, long propNum, PhaseInfo phase) {
    RoundInfo round = rounds.get(seqNum);
    if (round == null) {
      // unexpected
      return;
    }

    // re-send prepare if abort is regarding our last prepare req (the current proposal)
    // (if propNum < round.propNum this abort is outdated,
    // either due to message reordering of prepares or aborts)
    // (propNum shouldn't be > round.propNum)
    if (propNum == round.getPropNum()) {
      round.advanceProp(phase.propNum); // abandon this prop
      castPrepare(seqNum);

      round.updateParticipant(src, phase);
    }
    // safe to do this under all circumstances
    // though it's a little spammy if e.g. we've already sent an accept
  }
}
