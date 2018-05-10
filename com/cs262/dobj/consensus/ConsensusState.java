package com.cs262.dobj.consensus;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.Serializable;

import com.cs262.dobj.channel.*;

/*
 * State maintained by an instance of the consensus algorithm.
 * Abstract class since depending on the role of this instance, its behavior
 * in response to network messages might change.
 * Ledger addition handling and consequent core state updates are however
 * implemented here for consistency of semantics.
 * Similarly thread suspend-on-call and wake-on-response is implemented here.
 */
abstract class ConsensusState<ObjType extends Serializable> implements Serializable {
  // consensus state
  private ObjType inst; // instance of distributed object
  private ArrayList<Operation> ledger;

  // current peer set
  private class PeerInfo {
    public final String peerName;
    public final int peerPort;

    public PeerInfo(String name, int port) {
      this.peerName = name;
      this.peerPort = port;
    }
  }
  private HashMap<Long, PeerInfo> participants; // set of active participants in Paxos
  private long maxPeerId; // highest peer ID we've seen

  public ConsensusState(ObjType inst) {
    this.inst = inst;
    this.ledger = new ArrayList<Operation>();
    this.participants = new HashMap<>();
    this.maxPeerId = 0;
  }

  public ConsensusState(ConsensusState<ObjType> state) {
    this.inst = state.inst;
    this.ledger = state.ledger;
    this.participants = state.participants;
    this.maxPeerId = state.maxPeerId;
  }

  public synchronized ObjType getInstance() {
    return inst;
  }

  public synchronized void processMessage(ConsensusContext<ObjType> ctx,
                                          DistributedChannel<?, ConsensusMessage> channel,
                                          long src, ConsensusMessage mesg) {
    if (mesg instanceof RequestMessage) {
      processRequest(ctx, channel, src, (RequestMessage) mesg);
    } else if (mesg instanceof PrepareMessage) {
      processPrepare(ctx, channel, src, (PrepareMessage) mesg);
    } else if (mesg instanceof PromiseMessage) {
      processPromise(ctx, channel, src, (PromiseMessage) mesg);
    } else if (mesg instanceof AcceptMessage) {
      processAccept(ctx, channel, src, (AcceptMessage) mesg);
    } else if (mesg instanceof AcceptedMessage) {
      processAccepted(ctx, channel, src, (AcceptedMessage) mesg);
    } else if (mesg instanceof ChosenMessage) {
      processChosen(ctx, channel, src, (ChosenMessage) mesg);
    } else {
      // unknown message type; ignore
    }
  }

  protected abstract void processRequest(ConsensusContext<ObjType> ctx,
                                         DistributedChannel<?, ConsensusMessage> channel,
                                         long src, RequestMessage m);
  protected abstract void processPrepare(ConsensusContext<ObjType> ctx,
                                         DistributedChannel<?, ConsensusMessage> channel,
                                         long src, PrepareMessage m);
  protected abstract void processPromise(ConsensusContext<ObjType> ctx,
                                         DistributedChannel<?, ConsensusMessage> channel,
                                         long src, PromiseMessage m);
  protected abstract void processAccept(ConsensusContext<ObjType> ctx,
                                        DistributedChannel<?, ConsensusMessage> channel,
                                        long src, AcceptMessage m);
  protected abstract void processAccepted(ConsensusContext<ObjType> ctx,
                                          DistributedChannel<?, ConsensusMessage> channel,
                                          long src, AcceptedMessage m);

  protected void processChosen(ConsensusContext<ObjType> ctx,
                               DistributedChannel<?, ConsensusMessage> channel,
                               long src, ChosenMessage m) {
    // add to ledger
    // catchup: apply contiguous ledger ops to objects
  }
}
