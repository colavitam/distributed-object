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
    MyDumbClass instance = myContext.createDistributedInstance(MyDumbClass.class);
    instance.doSomething();
  }

  private class MyDumbClass {
    public void doSomething() {
      System.out.println("Something is done");
    }
  }
}