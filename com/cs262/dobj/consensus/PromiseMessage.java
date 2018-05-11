package com.cs262.dobj.consensus;

class PromiseMessage extends ConsensusMessage {
  public final long seqNum;
  public final long propNum; // promise up to this prop for recipient
  public final long earliestUnused; // no phase 2 occurred this round or after
  public final PhaseInfo phase; // last accepted proposal

  public PromiseMessage(long seqNum, long propNum, long earliestUnused, PhaseInfo phase) {
    this.seqNum = seqNum;
    this.propNum = propNum;
    this.earliestUnused = earliestUnused;
    this.phase = phase;
  }
}
