package com.cs262.dobj.consensus;

import java.lang.reflect.*;
import java.io.Serializable;

class PerformOperation extends Operation {
  public final long atomicOpNum; // atomic op this belongs to if any

  // invocation information
  public final Method method;
  public final Serializable[] args;

  public PerformOperation(long opNum, long atomicOpNum, Method method, Serializable[] args) {
    super(opNum);
    this.atomicOpNum = atomicOpNum;
    this.method = method;
    this.args = args;
  }
}
