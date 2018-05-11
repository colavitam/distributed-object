package com.cs262.dobj.consensus;

import java.io.Serializable;

class PhaseInfo implements Serializable {
  public final long src;
  public final long propNum;
  public final Operation accepted;

  public static PhaseInfo MIN_PHASE = new PhaseInfo(-1, -1, (Operation) null);

  public PhaseInfo(long src, long propNum, Operation op) {
    this.src = src;
    this.propNum = propNum;
    this.accepted = op;
  }

  public PhaseInfo(long src, long propNum, PhaseInfo prev) {
    this.src = src;
    this.propNum = propNum;
    this.accepted = prev.accepted;
  }

  public boolean precedes(long src, long propNum) {
    return this.propNum < propNum || (this.propNum == propNum && this.src < src);
  }

  public boolean precedes(PhaseInfo phase) {
    return this.propNum < phase.propNum || (this.propNum == phase.propNum && this.src < phase.src);
  }
}
