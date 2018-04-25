package com.cs262.dobj.test;

import com.cs262.dobj.*;
import java.io.*;

public class Main {
  public static void main(String[] args) throws IOException {
    BST<String, String> bst = new RedBlackBST<String, String>();
    DistributedContext<> myContext = new DistributedContext<>(9091, bst);
    BST<String, String> dbst = myContext.getDistributedInstance();

    System.out.println(dbst.isEmpty());
  }
}
