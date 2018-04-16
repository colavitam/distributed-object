package com.cs262.dobj;

import java.io.Serializable;

public interface DistributedChannel {
  public <T extends Serializable> void sendMessage(Message<T> message);
}
