package com.cs262.dobj.consensus;

class RequestMessage extends ConsensusMessage {
  public final Operation operation;

  public RequestMessage(Operation o) {
    operation = o;
  }
}
