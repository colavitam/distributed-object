package com.cs262.dobj.consensus;

class AbortAcceptMessage extends ConsensusMessage {
  public final long seqNum;
  public final long propNum;
  public final PhaseInfo phase;

  public AbortAcceptMessage(long seqNum, long propNum, PhaseInfo phase) {
    this.seqNum = seqNum;
    this.propNum = propNum;
    this.phase = phase;
  }
}
