package com.cs262.dobj;

import java.lang.reflect.*;
import java.io.Serializable;

public class DistributedContext {
  private DistributedChannel channel;
  
  public DistributedContext(DistributedChannel channel) {
    this.channel = channel;
  }

  private <T extends Serializable> InvocationHandler createHandler(T instance) {
    InvocationHandler handler = (Object proxy, Method method, Object[] args) -> {
      System.out.println("Intercepted!");
      return method.invoke(instance, args);
    };

    return handler;
  }

  public <T extends Serializable> T createDistributedInstance(T instance, Class<?>[] interfaces) {
    InvocationHandler handler = this.createHandler(instance);

    return (T) Proxy.newProxyInstance(DistributedContext.class.getClassLoader(), interfaces, handler);
  }
}
