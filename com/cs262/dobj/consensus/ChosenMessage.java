package com.cs262.dobj.consensus;

class ChosenMessage extends ConsensusMessage {
  public final long seqNum;
  public final Operation op;

  public ChosenMessage(long seqNum, Operation op) {
    this.seqNum = seqNum;
    this.op = op;
  }
}
