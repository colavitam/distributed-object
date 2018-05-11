package com.cs262.dobj.consensus;

import java.io.Serializable;

// TODO need to track source ID for unique operations
class Operation implements Serializable {
  public final long src; // client who requested operation
  public final long opNum; // client-side operation ID for callback association

  public Operation(long src, long opNum) {
    this.src = src;
    this.opNum = opNum;
  }

  public boolean isSame(long src, long opNum) {
    return this.src == src && this.opNum == opNum;
  }

  public boolean isSame(Operation op) {
    return op == null || this.src == op.src && this.opNum == op.opNum;
  }
}

// TODO operations:
// PerformOperation
// AtomicLeaseOperation
// NoOperation

