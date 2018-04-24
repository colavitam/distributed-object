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

  public long requestOperation() throws IOException {
    return consensus.propose(DistributedInstanceConsensusMessage.requestMessage());
  }

  public void completeOperation(long operationNumber) throws IOException {
    consensus.propose(DistributedInstanceConsensusMessage.completeMessage(operationNumber));
  }

  public void performOperation(Method method, Serializable[] args, long operationNumber) throws IOException {
    consensus.propose(DistributedInstanceConsensusMessage.performMessage(method, args, operationNumber));
  }
}
