package com.cs262.dobj.consensus;

import java.lang.reflect.*;
import java.io.Serializable;

// encode performance of one or more operations
class PerformOperation extends Operation {
  public final long atomicOpNum; // atomic op this belongs to if any

  // invocation information
  public final Method[] method;
  public final Serializable[][] args;

  public PerformOperation(long src, long opNum, long atomicOpNum, Method method, Serializable[] args) {
    super(src, opNum);
    this.atomicOpNum = atomicOpNum;
    this.method = new Method[1];
    this.method[0] = method;
    this.args = new Serializable[1][];
    this.args[0] = args;
  }

  public PerformOperation(long src, long opNum, long atomicOpNum, Method[] methods, Serializable[][] argss) {
    super(src, opNum);
    this.atomicOpNum = atomicOpNum;
    this.method = methods;
    this.args = argss;
  }
}
