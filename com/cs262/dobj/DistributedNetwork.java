package com.cs262.dobj;

public interface DistributedNetwork {
  public void broadcastMessage(Message<T> message);
  public void sendMessage(Message<T> message, long destination) throws IOException;
}
