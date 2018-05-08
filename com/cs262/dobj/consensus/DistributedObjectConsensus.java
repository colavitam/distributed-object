package com.cs262.dobj.consensus;

import java.util.HashMap;
import java.io.*;
import java.lang.reflect.*;

public class DistributedObjectConsensus<ObjType extends Serializable> {
  private DistributedObjectChannel channel; // communications channel
  private ConsensusState state;

  public class ConsensusState {
    public ObjType inst; // instance of distributed object
    public HashMap<Long, PeerInfo> participants; // set of active participants in Paxos

    public long currentOperator;
    public long maxPeerId; // highest peer ID we've seen
    public long atomicOpNum; // are we currently inside an atomic operation?
    // TODO ledger state
  }

  private class PeerInfo {
    public final String peerName;
    public final int peerPort;

    public PeerInfo(String name, int port) {
      this.peerName = name;
      this.peerPort = port;
    }
  }

  public DistributedObjectConsensus(ObjType inst, int serverPort) throws IOException {
    this.channel = new DistributedObjectChannel(this, serverPort);

    state = new ConsensusState();
    state.inst = inst;
    state.participants = new HashMap<>();

    state.currentOperator = -1;
    state.maxPeerId = 0;
    state.atomicOpNum = -1;
  }

  public DistributedObjectConsensus(String hostName, int hostPort, String serverName, int serverPort, long id) throws Exception {
    this.channel = new DistributedObjectChannel(this, hostName, hostPort, serverName, serverPort, id);
  }

  public void setState(ConsensusState state) {
    this.state = state;
  }

  public ConsensusState getState() {
    return state;
  }

  public ObjType getInstance() {
    return state.inst;
  }

  public synchronized void processMessage(long src, Message mesg) {

  }

  // once we return, state machine is in receptive state for performOperation calls from this participant
  public long requestOperation() throws IOException {
    //return consensus.propose(DistributedObjectConsensusMessage.requestMessage());
    return 0;
  }

  public void performOperation(Method method, Serializable[] args, long operationNumber) throws IOException {
    //consensus.propose(DistributedObjectConsensusMessage.performMessage(method, args, operationNumber));
  }

  public void completeOperation(long operationNumber) throws IOException {
    //consensus.propose(DistributedObjectConsensusMessage.completeMessage(operationNumber));
  }

  // register a new peer joining the group
  public void registerJoin(long peerId, String peerHost, int peerPort) {
  }

  public class Message {

  }
}
