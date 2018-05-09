package com.cs262.dobj.consensus;

import java.io.Serializable;

class Operation implements Serializable {
  public final long opNum; // client-side operation ID for callback association

  public Operation(long opNum) {
    this.opNum = opNum;
  }
}
