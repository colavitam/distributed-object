package com.cs262.dobj;

import java.lang.reflect.*;
import java.io.Serializable;
import com.cs262.dobj.consensus.*;

public class DistributedContext {
  private DistributedChannel channel;
  private DistributedInstanceConsensusProtocol consensusProtocol;
  
  public DistributedContext(DistributedChannel channel) {
    this.channel = channel;
    this.consensusProtocol = null;
  }

  private <T extends Serializable> InvocationHandler createHandler(T instance) {
    InvocationHandler handler = (Object proxy, Method method, Object[] args) -> {
      System.out.println("Intercepted!");
      long opNum = consensusProtocol.requestOperation(false);
      consensusProtocol.performOperation(method, (Serializable[]) args, opNum);
      Object result = method.invoke(instance, args);
      consensusProtocol.completeOperation();

      return result;
    };

    return handler;
  }

  public <T extends Serializable> T createDistributedInstance(T instance, Class<?>[] interfaces) {
    InvocationHandler handler = this.createHandler(instance);

    return (T) Proxy.newProxyInstance(DistributedContext.class.getClassLoader(), interfaces, handler);
  }
}
