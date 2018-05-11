package com.cs262.dobj.consensus;

class AbortPrepareMessage extends ConsensusMessage {
  public final long seqNum;
  public final long propNum;
  public final PhaseInfo phase; // current state

  public AbortPrepareMessage(long seqNum, long propNum, PhaseInfo phase) {
    this.seqNum = seqNum;
    this.propNum = propNum;
    this.phase = phase;
  }
}
