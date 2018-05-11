package com.cs262.dobj.consensus;

class LeaderMessage extends ConsensusMessage {
  public final long leaderId;

  public LeaderMessage(long leaderId) {
    this.leaderId = leaderId;
  }
}
