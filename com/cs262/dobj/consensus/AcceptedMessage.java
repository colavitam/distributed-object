package com.cs262.dobj.consensus;

class AcceptedMessage extends ConsensusMessage {
  public final long seqNum;
  public final long propNum;

  public AcceptedMessage(long seqNum, long propNum) {
    this.seqNum = seqNum;
    this.propNum = propNum;
  }
}
