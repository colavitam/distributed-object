package com.cs262.dobj.consensus;

import java.lang.reflect.*;
import java.io.Serializable;

// encode performance of one or more operations
class AtomicLeaseOperation extends Operation {
  public AtomicLeaseOperation(long src, long opNum) {
    super(src, opNum);
  }
}
