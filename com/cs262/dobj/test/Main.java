package com.cs262.dobj.test;

import com.cs262.dobj.*;
import java.io.*;

public class Main {
  public static void main(String[] args) throws IOException {
    DistributedContext<BST<String, String>> myContext = new DistributedContext<>(9091, new RedBlackBST<String, String>());
    BST<String, String> bst = myContext.getDistributedInstance();

    System.out.println(bst.isEmpty());
  }
}
