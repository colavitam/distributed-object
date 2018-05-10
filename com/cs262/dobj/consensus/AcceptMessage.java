package com.cs262.dobj.consensus;

class AcceptMessage extends ConsensusMessage {
  public final long seqNum;
  public final long propNum;
  public final Operation op;

  public AcceptMessage(long seqNum, long propNum, Operation op) {
    this.seqNum = seqNum;
    this.propNum = propNum;
    this.op = op;
  }
}
