package com.cs262.dobj.consensus;

import java.util.*;
import java.util.Map.Entry;
import java.io.Serializable;

/*
 * State maintained by an instance of the consensus algorithm.
 * Abstract class since depending on the role of this instance, its behavior
 * in response to network messages might change.
 * Ledger addition handling and consequent core state updates are however
 * implemented here for consistency of semantics.
 * Similarly thread suspend-on-call and wake-on-response is implemented here.
 */
abstract class ConsensusState<ObjType extends Serializable> implements Serializable {
  // context
  protected transient ConsensusContext<ObjType> ctx;

  // consensus object state
  private ObjType inst; // instance of distributed object

  // current peer set
  private static class PeerInfo {
    public String peerName;
    public int peerPort;
    public long startRound; // round this peer participates after
    public long endRound;   // first round this peer should not participate in

    public PeerInfo(String name, int port, long startRound) {
      this.peerName = name;
      this.peerPort = port;
      this.startRound = startRound;
      this.endRound = Long.MAX_VALUE;
    }
  }
  private HashMap<Long, PeerInfo> participants; // set of active participants in Paxos
  private long maxPeerId; // highest peer ID we've seen

  // ledger info
  private HashMap<Long, Operation> ledger;
  private long checkpoint; // which sequence number is our object consistent with?

  public ConsensusState(ObjType inst) {
    this.inst = inst;
    this.ledger = new HashMap<>();
    this.participants = new HashMap<>();
    this.maxPeerId = 0;
  }

  public ConsensusState(ConsensusState<ObjType> state) {
    this.inst = state.inst;
    this.ledger = state.ledger;
    this.participants = state.participants;
    this.maxPeerId = state.maxPeerId;
  }

  public synchronized void setContext(ConsensusContext<ObjType> ctx) {
    this.ctx = ctx;
  }

  public synchronized ObjType getInstance() {
    return inst;
  }

  public synchronized long getCheckpoint() {
    return checkpoint;
  }

  // get set of participants at given round number
  public synchronized Set<Long> getParticipants(long seqNum) {
    // TODO make this more efficient
    HashSet<Long> parts = new HashSet<>();
    for (Entry<Long, PeerInfo> peer : participants.entrySet()) {
      if (peer.getValue().startRound >= seqNum && seqNum < peer.getValue().endRound) {
        parts.add(peer.getKey());
      }
    }
    return parts;
  }

  public synchronized Set<Long> getParticipants() {
    return participants.keySet();
  }

  public synchronized void processMessage(long src, ConsensusMessage mesg) {
    if (mesg instanceof RequestMessage) {
      processRequest(src, (RequestMessage) mesg);
    } else if (mesg instanceof PrepareMessage) {
      processPrepare(src, (PrepareMessage) mesg);
    } else if (mesg instanceof AbortPrepareMessage) {
      processAbortPrepare(src, (AbortPrepareMessage) mesg);
    } else if (mesg instanceof PromiseMessage) {
      processPromise(src, (PromiseMessage) mesg);
    } else if (mesg instanceof AcceptMessage) {
      processAccept(src, (AcceptMessage) mesg);
    } else if (mesg instanceof AbortAcceptMessage) {
      processAbortAccept(src, (AbortAcceptMessage) mesg);
    } else if (mesg instanceof AcceptedMessage) {
      processAccepted(src, (AcceptedMessage) mesg);
    } else if (mesg instanceof ChosenMessage) {
      processChosen(src, (ChosenMessage) mesg);
    } else {
      // unknown message type; ignore
    }
  }

  // for timeout use
  protected synchronized void processEvent() {

  }

  protected void processRequest(long src, RequestMessage m) { }

  protected void processPrepare(long src, PrepareMessage m) { }

  protected void processPromise(long src, PromiseMessage m) { }
  protected void processAbortPrepare(long src, AbortPrepareMessage m) { }

  protected void processAccept(long src, AcceptMessage m) { }
  protected void processAbortAccept(long src, AbortAcceptMessage m) { }

  protected void processAccepted(long src, AcceptedMessage m) { }

  private void processChosen(long src, ChosenMessage m) {
    // add to ledger
    // catchup: apply contiguous ledger ops to objects
  }

  private void applyOperation(Operation op) {

  }
}
