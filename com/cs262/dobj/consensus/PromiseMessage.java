package com.cs262.dobj.consensus;

class PromiseMessage extends ConsensusMessage {
  public final long seqNum;
  public final long propNum;
  public final boolean hasAccepted;
  public final Operation accepted;

  public PromiseMessage(long seqNum, long propNum) {
    this.seqNum = seqNum;
    this.propNum = propNum;
    this.hasAccepted = false;
    this.accepted = null;
  }

  public PromiseMessage(long seqNum, long propNum, Operation op) {
    this.seqNum = seqNum;
    this.propNum = propNum;
    this.hasAccepted = true;
    this.accepted = op;
  }
}
