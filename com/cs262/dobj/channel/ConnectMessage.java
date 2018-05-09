package com.cs262.dobj.channel;

import java.io.Serializable;

// coordination message when connecting to joined peer
class ConnectMessage extends ChannelMessage {
  public ConnectMessage(long src) {
    super(src);
  }
}
