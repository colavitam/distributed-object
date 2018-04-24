package com.cs262.dobj;

import java.lang.reflect.*;
import java.io.*;
import com.cs262.dobj.consensus.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

public class DistributedContext {
  private DistributedChannel channel;
  private DistributedInstanceConsensusProtocol consensusProtocol;
  private long atomicOpNum = -1;
  private Lock atomicLock;
  
  public DistributedContext(DistributedChannel channel) {
    this.channel = channel;
    this.consensusProtocol = null;
    this.atomicLock = new ReentrantLock();
  }

  private <T extends Serializable> InvocationHandler createHandler(T instance) {
    InvocationHandler handler = (Object proxy, Method method, Object[] args) -> {
      long opNum;
      Object result;

      atomicLock.lock();

      if (atomicOpNum != -1)
        opNum = atomicOpNum;
      else
        opNum = consensusProtocol.requestOperation();

      consensusProtocol.performOperation(method, (Serializable[]) args, opNum); // TODO: lockstep for atomic?

      try {
        result = method.invoke(instance, args);
      } finally {
        if (atomicOpNum == -1)
          consensusProtocol.completeOperation(opNum);
        atomicLock.unlock();
      }

      return result;
    };

    return handler;
  }

  public <T extends Serializable> T createDistributedInstance(T instance, Class<?>[] interfaces) {
    InvocationHandler handler = this.createHandler(instance);

    return (T) Proxy.newProxyInstance(DistributedContext.class.getClassLoader(), interfaces, handler);
  }

  public <T> T atomicOperation(Supplier<T> operation) throws IOException {
    // TODO: recursive atomic?
    T result;

    try {
      atomicLock.lock();
      this.atomicOpNum = consensusProtocol.requestOperation();
      result = operation.get();
    } finally {
      consensusProtocol.completeOperation(opNum);

      atomicOpNum = -1;
      atomicLock.unlock();
    }

    return result;
  }
}
