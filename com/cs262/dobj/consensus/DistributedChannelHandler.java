package com.cs262.dobj.consensus;

import java.io.Serializable;

/*
 * distributed channel is for someone who:
 * - receives some lump state on joining
 * - thereafter can process incoming messages
 * - transmits state to someone who joins
 * - wants to know when a peer has joined
 */
public interface DistributedChannelHandler<State extends Serializable, Message extends Serializable> {
  public State getState();
  public void setState(State state);
  public void processMessage(long src, Message mesg);
  public void registerPeer(long peerId, String peerHost, int peerPort); // TODO assign ID
}
