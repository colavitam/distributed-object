package com.cs262.dobj.consensus;

class AcceptedMessage extends ConsensusMessage {
  public final long seqNum;
  public final Operation op;
  public final PhaseInfo phase; // TODO this should be the now current state

  public AcceptedMessage(long seqNum, Operation op, PhaseInfo phase) {
    this.seqNum = seqNum;
    this.op = op;
    this.phase = phase;
  }
}
