package com.cs262.dobj.consensus;

import java.io.Serializable;

// coordination message in response to join request
// "hello, you've connected to ID ..."
// "our current distributed consensus state is ..."
public class WelcomeMessage<T extends Serializable> extends ChannelMessage {
  public final T state;

  public WelcomeMessage(long src, T state) {
    super(src);
    this.state = state;
  }
}
