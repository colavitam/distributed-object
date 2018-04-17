package com.cs262.dobj.test;

import com.cs262.dobj.*;
import java.io.*;

public class Main {
  public static void main(String[] args) throws IOException {
    DistributedChannel myChannel = new SocketDistributedChannel(9091);
    DistributedContext myContext = new DistributedContext(myChannel);
    
    BST<String, String> bst = new RedBlackBST<String, String>();
    BST<String, String> dbst = myContext.createDistributedInstance(bst, bst.getClass().getInterfaces());
    System.out.println(dbst.isEmpty());
  }
}