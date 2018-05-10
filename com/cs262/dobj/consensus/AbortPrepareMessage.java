package com.cs262.dobj.consensus;

class AbortPrepareMessage extends ConsensusMessage {
  public final long seqNum;
  public final long propNum;

  public AbortPrepareMessage(long seqNum, long propNum) {
    this.seqNum = seqNum;
    this.propNum = propNum;
  }
}
