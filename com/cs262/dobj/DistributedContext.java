package com.cs262.dobj;

import java.lang.reflect.*;
import java.io.*;
import java.util.concurrent.locks.*;
import java.util.function.*;

/*
 * A DistributedContext represents shared state for one distributed object.
 *
 * An instance contains the state required to act as a replicated server
 * with consistency provided by Paxos:
 *  - state of the distributed object
 *  - Paxos protocol information
 *  - communication configuration
 * This object serves as the central dispatch for invocations on the 
 */
public class DistributedContext<ObjType extends Serializable> {
  /* server-replicated state */
  private ObjType dobj; // instance of distributed object
  private HashMap<Long, PeerInfo> peerSet; // set of live replicas
  private long maxPeerId; // highest peer ID we've seen
  private long atomicOpNum = -1; // are we currently inside an atomic operation?

  /* local state/helper objects */
  private Lock atomicLock; // lock for concurrent atomic operations
  private DistributedConsensusProtocol consensusProtocol; // consensus protocol state
  private DistributedObjectChannel channel; // communication channel

  /* create a new context and act as a server on the given port */
  public DistributedContext(int serverPort, ObjType instance) {
    this.dobj = instance;
    this.peerSet = new HashSet<>();
    this.atomicOpNum = -1;

    this.atomicLock = new ReentrantLock();
    this.consensusProtocol = null;
    this.channel = new DistributedObjectChannel(this, serverPort);
  }

  /* attempt to join to existing context at the given host, and act as a server on the given port */
  public DistributedContext(int serverPort, String hostName, int hostPort, ObjType instance) {
    this.dobj = instance;
    this.peerSet = new HashSet<>();
    this.atomicOpNum = -1;

    this.atomicLock = new ReentrantLock();
    this.consensusProtocol = null;
    this.channel = new DistributedObjectChannel(serverPort, hostName, hostPort);
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
  
  public void doWelcome(WelcomeMessage wm) {

  }

  public void receivedMessage(Message m) {
    if (m instanceof WelcomeMessage) {
      // TODO catch our state up
    } else if (m instanceof JoinMessage) {
      // TODO mediate join
    } else if (m instanceof PaxosMessage) {
      // TODO route to consensus system
    }
  }
}
