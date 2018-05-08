package com.cs262.dobj.consensus;

import java.io.Serializable;

public class ChannelMessage implements Serializable {
  public final long src;
  // TODO uid, dst for a flood message

  public ChannelMessage(long src) {
    this.src = src;
  }
}
