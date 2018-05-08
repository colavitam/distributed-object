package com.cs262.dobj.consensus;

import java.io.Serializable;

// coordination message in response to join request
// "hello, you've connected to ID ..."
// "our current distributed consensus state is ..."
public class WelcomeMessage extends ChannelMessage {
  public final DistributedObjectConsensus.ConsensusState state;

  public WelcomeMessage(long src, DistributedObjectConsensus.ConsensusState state) {
    super(src);
    this.state = state;
  }
}
