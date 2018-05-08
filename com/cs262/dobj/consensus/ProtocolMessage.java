package com.cs262.dobj.consensus;

import java.io.Serializable;

// wrapper for messages sent by higher-level consensus protocol
public class ProtocolMessage<T extends Serializable> extends ChannelMessage {
  public final T mesg;

  public ProtocolMessage(long src, T mesg) {
    super(src);
    this.mesg = mesg;
  }
}
