package com.cs262.dobj.consensus;

import java.util.HashMap;
import java.io.Serializable;

import com.cs262.dobj.channel.*;

class LeaderConsensusState<ObjType extends Serializable> extends ConsensusState<ObjType> {
  private static class RoundInfo {
  }
  private HashMap<Long, RoundInfo> rounds;

  public LeaderConsensusState(ObjType inst) {
    super(inst);
  }

  protected void processRequest(ConsensusContext<ObjType> ctx,
                                DistributedChannel<?, ConsensusMessage> channel,
                                long src, RequestMessage m) {
    // make paxos assignments and begin rounds if necessary
  }

  protected void processPrepare(ConsensusContext<ObjType> ctx,
                                DistributedChannel<?, ConsensusMessage> channel,
                                long src, PrepareMessage m) {
    // reply with a promise
  }
  
  protected void processPromise(ConsensusContext<ObjType> ctx,
                                DistributedChannel<?, ConsensusMessage> channel,
                                long src, PromiseMessage m) {
    // register promise from node
  }

  protected void processAccept(ConsensusContext<ObjType> ctx,
                               DistributedChannel<?, ConsensusMessage> channel,
                               long src, AcceptMessage m) {
    // reply with an accepted ack if appropriate
  }

  protected void processAccepted(ConsensusContext<ObjType> ctx,
                                 DistributedChannel<?, ConsensusMessage> channel,
                                 long src, AcceptedMessage m) {
    // register acceptance from node
  }
}
