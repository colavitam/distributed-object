package com.cs262.dobj;

import java.lang.reflect.*;

public class DistributedContext {
  private DistributedChannel channel;
  
  public DistributedContext(DistributedChannel channel) {
    this.channel = channel;
  }

  private <T> InvocationHandler createHandler(T instance) {
    InvocationHandler handler = (Object proxy, Method method, Object[] args) -> {
      System.out.println("Intercepted!");
      return method.invoke(instance, args);
    };

    return handler;
  }

  public <T> T createDistributedInstance(T instance, Class<?>[] interfaces) {
    InvocationHandler handler = this.createHandler(instance);

    return (T) Proxy.newProxyInstance(DistributedContext.class.getClassLoader(), interfaces, handler);
  }
}
