package com.cs262.dobj.consensus;

class PrepareMessage extends ConsensusMessage {
  public final long seqNum;
  public final long propNum;

  public PrepareMessage(long seqNum, long propNum) {
    this.seqNum = seqNum;
    this.propNum = propNum;
  }
}
