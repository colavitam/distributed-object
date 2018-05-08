package com.cs262.dobj.consensus;

import java.lang.reflect.*;
import java.io.*;

// messages sent by the distributed instance consensus protocol over 
public class ConsensusMessage implements Serializable {
  public enum ConsensusMessageType {
    DIC_REQUEST_OPERATION,
    DIC_PERFORM_OPERATION,
    DIC_COMPLETE_OPERATION
  }

  private ConsensusMessageType messageType;

  // for perform messages
  private Method method;
  private Serializable[] args;

  // for perform and complete messages
  private long operationNumber;

  public static ConsensusMessage requestMessage() {
    return new ConsensusMessage(ConsensusMessageType.DIC_REQUEST_OPERATION, null, null, 0);
  }

  public static ConsensusMessage performMessage(Method method, Serializable[] args, long operationNumber) {
    return new ConsensusMessage(ConsensusMessageType.DIC_PERFORM_OPERATION, method, args, operationNumber);
  }

  public static ConsensusMessage completeMessage(long operationNumber) {
    return new ConsensusMessage(ConsensusMessageType.DIC_COMPLETE_OPERATION, null, null, operationNumber);
  }

  private ConsensusMessage(ConsensusMessageType type, Method method, Serializable[] args, long operationNumber) {
    this.messageType = type;
    this.method = method;
    this.args = args;
    this.operationNumber = operationNumber;
  }

  public ConsensusMessageType getType() {
    return messageType;
  }

  public Method getMethod() {
    return method;
  }

  public Serializable[] getArgs() {
    return args;
  }

  public long getOperationNumber() {
    return operationNumber;
  }
}
