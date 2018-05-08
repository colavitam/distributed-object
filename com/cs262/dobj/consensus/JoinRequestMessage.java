package com.cs262.dobj.consensus;

import java.io.Serializable;

// coordination message when joining a channel
// "hello, my ID is..."
// "you can contact me at this hostname and port"
public class JoinRequestMessage extends ChannelMessage {
  public final String name;
  public final int port;

  public JoinRequestMessage(long src, String name, int port) {
    super(src);
    this.name = name;
    this.port = port;
  }
}
