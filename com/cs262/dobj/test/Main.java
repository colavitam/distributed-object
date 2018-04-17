package com.cs262.dobj.test;

import com.cs262.dobj.*;
import java.io.Serializable;

public class Main {
  public static void main(String[] args) {
    BST<String, String> bst = new RedBlackBST<String, String>();

    DistributedChannel myChannel = new DistributedChannel() {
      public <T extends Serializable> void sendMessage(Message<T> message) {
        System.out.println("Sent message");
      }
    };

    DistributedContext myContext = new DistributedContext(myChannel);

    BST<String, String> dbst = myContext.createDistributedInstance(bst, bst.getClass().getInterfaces());
    System.out.println(dbst.isEmpty());
  }
}