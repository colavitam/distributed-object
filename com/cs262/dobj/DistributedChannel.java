package com.cs262.dobj;

import java.io.*;

public interface DistributedChannel {
  public long registerPeer(String hostname, int port) throws IOException;
  public <T extends Serializable> void sendMessage(Message<T> message, long destination) throws IOException;
  public void registerMessageReceiptHandler(MessageReceiptHandler handler);
}
