package com.cs262.dobj.consensus;

import java.io.Serializable;

// wrapper for messages sent by higher-level consensus protocol
public class ConsensusMessage extends ChannelMessage {
  public final DistributedObjectConsensus.Message mesg;

  public ConsensusMessage(long src, DistributedObjectConsensus.Message mesg) {
    super(src);
    this.mesg = mesg;
  }
}
