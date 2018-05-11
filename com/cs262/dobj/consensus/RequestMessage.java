package com.cs262.dobj.consensus;

class RequestMessage extends ConsensusMessage {
  public final Operation op;

  public RequestMessage(Operation op) {
    this.op = op;
  }
}
