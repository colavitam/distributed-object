package com.cs262.dobj;

import java.lang.reflect.*;
import java.io.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

import com.cs262.dobj.consensus.DistributedObjectConsensus;

/*
 * A DistributedContext serves as the central dispatch for invocations on the distributed object.
 */
public class DistributedContext<ObjType extends Serializable> {
  private DistributedObjectConsensus<ObjType> consensus; // server-replicated state
  private ObjType inst; // distributed instance
  private Lock atomicLock; // lock for concurrent atomic operations

  /* create a new context and act as a server on the given port */
  public DistributedContext(int serverPort, ObjType inst) throws IOException {
    this.consensus = new DistributedObjectConsensus(inst, serverPort);
    this.inst = inst;

    this.atomicLock = new ReentrantLock();
  }

  /* attempt to join to existing context at the given host,
   * and act as a server on the given port */
  public DistributedContext(String hostName, int hostPort, int serverPort) throws Exception {
    this.consensus = new DistributedObjectConsensus(hostName, hostPort, "localhost", serverPort, 1);
    this.inst = consensus.getInstance();

    this.atomicLock = new ReentrantLock();
  }

  private InvocationHandler createHandler(ObjType instance) {
    InvocationHandler handler = (Object proxy, Method method, Object[] args) -> {
      long opNum;
      Object result;

      System.out.printf("invoked method %s\n", method.getName());

      atomicLock.lock();

      long atomicOpNum = 0; // TODO
      if (atomicOpNum != -1)
        opNum = atomicOpNum;
      else
        opNum = consensus.requestOperation();

      consensus.performOperation(method, (Serializable[]) args, opNum); // TODO: lockstep for atomic?

      try {
        result = method.invoke(instance, args);
      } finally {
        if (atomicOpNum == -1)
          consensus.completeOperation(opNum);
        atomicLock.unlock();
      }

      return result;
    };

    return handler;
  }

  public ObjType getDistributedInstance() {
    Class<?>[] interfaces = inst.getClass().getInterfaces();
    InvocationHandler handler = this.createHandler(inst);

    return (ObjType) Proxy.newProxyInstance(DistributedContext.class.getClassLoader(), interfaces, handler);
  }

  public <T> T atomicOperation(Supplier<T> operation) throws IOException {
    // TODO: recursive atomic?
    T result;

    long opNum = 0; // TODO
    long atomicOpNum = 0; // TODO
    try {
      atomicLock.lock();
      atomicOpNum = consensus.requestOperation();
      result = operation.get();
    } finally {
      consensus.completeOperation(opNum);

      atomicOpNum = -1;
      atomicLock.unlock();
    }

    return result;
  }
}
