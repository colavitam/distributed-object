package com.cs262.dobj.consensus;

import java.util.HashMap;
import java.io.Serializable;

public class ConsensusState<ObjType extends Serializable> implements Serializable {
  private class PeerInfo {
    public final String peerName;
    public final int peerPort;

    public PeerInfo(String name, int port) {
      this.peerName = name;
      this.peerPort = port;
    }
  }

  public ObjType inst; // instance of distributed object
  public HashMap<Long, PeerInfo> participants; // set of active participants in Paxos

  public long currentOperator;
  public long maxPeerId; // highest peer ID we've seen
  public long atomicOpNum; // are we currently inside an atomic operation?
  // TODO ledger state
}
