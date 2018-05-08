package com.cs262.dobj.consensus;

import java.io.Serializable;

// coordination message when connecting to joined peer
public class ConnectionMessage extends ChannelMessage {
  public ConnectionMessage(long src) {
    super(src);
  }
}
