package com.cs262.dobj;

import java.io.Serializable;

public class Message implements Serializable {
  public final long src; // who am I from?
  // TODO uid and dst for a contagious message

  public Message(long src) {
    this.src = src;
  }
}
