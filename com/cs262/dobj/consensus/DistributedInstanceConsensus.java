package com.cs262.dobj.consensus;

import java.io.*;
import java.lang.reflect.*;

public class DistributedInstanceConsensus {
  private DistributedChannel ch;
  private ConsensusProtocol consensus;

  public DistributedInstanceConsensus(DistributedChannel ch) {
    this.channel = ch;
    this.consensus = new Paxos(ch);
  }

  // once we return, state machine is in receptive state for performOperation calls from this participant
  public long requestOperation() throws IOException {
    return consensus.propose(DistributedInstanceConsensusMessage.requestMessage());
  }

  public void performOperation(Method method, Serializable[] args, long operationNumber) throws IOException {
    consensus.propose(DistributedInstanceConsensusMessage.performMessage(method, args, operationNumber));
  }

  public void completeOperation(long operationNumber) throws IOException {
    consensus.propose(DistributedInstanceConsensusMessage.completeMessage(operationNumber));
  }
}
