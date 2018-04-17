package com.cs262.dobj;

import java.io.Serializable;

public class Message<T extends Serializable> implements Serializable {
  private String id;
  private T content;

  public Message(String id, T content) {
    this.id = id;
    this.content = content;
  }
}
