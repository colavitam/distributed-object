package com.cs262.dobj.consensus;

import java.util.HashMap;
import java.lang.reflect.*;
import java.io.*;

import com.cs262.dobj.channel.*;

public class ConsensusContext<ObjType extends Serializable>
  implements DistributedChannelHandler<ConsensusState<ObjType>, ConsensusMessage>
{
  private DistributedChannel<ConsensusState<ObjType>, ConsensusMessage> channel;
  private ConsensusState<ObjType> state;

  public ConsensusContext(ObjType inst, int serverPort) throws IOException {
    channel = new DistributedChannel<ConsensusState<ObjType>, ConsensusMessage>(this, serverPort);
    state = new LeaderConsensusState(inst);
  }

  public ConsensusContext(String hostName, int hostPort, String serverName, int serverPort, long id) throws Exception {
    channel = new DistributedChannel<ConsensusState<ObjType>, ConsensusMessage>(this, hostName, hostPort, serverName, serverPort, id);
  }

  public synchronized ConsensusState<ObjType> getState() {
    return new FollowerConsensusState(state);
  }

  public synchronized void setState(ConsensusState<ObjType> state) {
    this.state = state;
  }

  public synchronized void processMessage(long src, ConsensusMessage mesg) {
    state.processMessage(this, channel, src, mesg);
  }

  public synchronized ObjType getInstance() {
    return state.getInstance();
  }

  // register a new peer joining the group
  public void registerPeer(long peerId, String peerHost, int peerPort) {
  }

  // once we return, state machine is in receptive state for performOperation calls from this participant
  public long requestOperation() throws IOException {
    return 0;
  }

  public void performOperation(Method method, Serializable[] args, long opNum) throws IOException {
  }

  public void completeOperation(long opNum) throws IOException {
  }
}
