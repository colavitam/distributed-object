package com.cs262.dobj.consensus;

import java.util.HashMap;
import java.io.Serializable;

import com.cs262.dobj.channel.*;

class ConsensusState<ObjType extends Serializable> implements Serializable {
  private ConsensusContext<ObjType> ctx;
  private DistributedChannel<?, ConsensusMessage> channel;

  private class PeerInfo {
    public final String peerName;
    public final int peerPort;

    public PeerInfo(String name, int port) {
      this.peerName = name;
      this.peerPort = port;
    }
  }

  private ObjType inst; // instance of distributed object
  private HashMap<Long, PeerInfo> participants; // set of active participants in Paxos

  private long maxPeerId; // highest peer ID we've seen
  private long atomicOpNum; // are we currently inside an atomic operation?
  // TODO ledger state

  public ConsensusState(ConsensusContext<ObjType> ctx, DistributedChannel<?, ConsensusMessage> channel, ObjType inst) {
    this.ctx = ctx;
    this.channel = channel;
    this.inst = inst;
    this.participants = new HashMap<>();

    this.maxPeerId = 0;
    this.atomicOpNum = -1;
  }

  public ObjType getInstance() {
    return inst;
  }

  public void processRequest(Operation op) {
    if (op instanceof PerformOperation) {

    }
  }
}
