package com.cs262.dobj.test;

import com.cs262.dobj.*;
import java.io.*;

public class Main {
  public static void main(String[] args) throws IOException {
    DistributedObject<BST<String, String>> distributor = new DistributedObject<>(9091, new RedBlackBST<String, String>());
    BST<String, String> bst = distributor.getDistributedInstance();

    System.out.println(bst.isEmpty());
  }
}
