package com.cs262.dobj.channel;

import java.io.Serializable;

// coordination message in response to join request
// "hello, you've connected to ID ..."
// "our current distributed consensus state is ..."
class WelcomeMessage<T extends Serializable> extends ChannelMessage {
  public final T state;

  public WelcomeMessage(long src, T state) {
    super(src);
    this.state = state;
  }
}
