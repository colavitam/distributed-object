package com.cs262.dobj.consensus;

class AbortAcceptMessage extends ConsensusMessage {
  public final long seqNum;
  public final long propNum;

  public AbortAcceptMessage(long seqNum, long propNum) {
    this.seqNum = seqNum;
    this.propNum = propNum;
  }
}
