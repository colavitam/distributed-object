package com.cs262.dobj;

import java.io.Serializable;

public interface DistributedChannel {
  public long registerPeer(String hostname);
  public <T extends Serializable> void sendMessage(Message<T> message, long destination);
  public void registerMessageReceiptHandler(MessageReceiptHandler handler);
}
