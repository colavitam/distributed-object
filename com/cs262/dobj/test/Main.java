package com.cs262.dobj.test;

import com.cs262.dobj.*;
import java.io.Serializable;

public class Main {
  public static void main(String[] args) {
    DistributedChannel myChannel = new DistributedChannel() {
      public <T extends Serializable> void sendMessage(Message<T> message) {
        System.out.println("Sent message");
      }
    };
    DistributedContext myContext = new DistributedContext(myChannel);
    Empty instance = myContext.createDistributedInstance(new MyDumbClass(), new Class<?>[] { Empty.class });
    instance.doSomething();
  }


  public static interface Empty {
    public void doSomething();
  }

  public static class MyDumbClass implements Empty, Serializable {
    public void doSomething() {
      System.out.println("Something is done");
    }
  }
}