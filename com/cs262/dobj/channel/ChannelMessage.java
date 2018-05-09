package com.cs262.dobj.channel;

import java.io.Serializable;

class ChannelMessage implements Serializable {
  public final long src;
  // TODO uid, dst for a flood message

  public ChannelMessage(long src) {
    this.src = src;
  }
}
