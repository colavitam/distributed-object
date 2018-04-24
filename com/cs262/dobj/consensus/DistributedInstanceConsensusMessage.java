
// messages sent by the distributed instance consensus protocol over 
public class DistributedInstanceConsensusMessage {
  public enum DistributedInstanceConsensusMessageType {
    DIC_REQUEST_OPERATION,
    DIC_PERFORM_OPERATION,
    DIC_COMPLETE_OPERATION
  }

  private DistributedInstanceConsensusMessageType messageType;

  // for perform messages
  private Method method;
  private Serializable[] args;

  // for perform and complete messages
  private long operationNumber;

  public static requestMessage() {
    return new DistributedInstanceConsensusMessage(DIC_REQUEST_OPERATION, null, null, null);
  }

  public static performMessage(Method method, Serializable[] args, long operationNumber) {
    return new DistributedInstanceConsensusMessage(DIC_PERFORM_OPERATION, method, args, operationNumber);
  }

  public static completeMessage(long operationNumber) {
    return new DistributedInstanceConsensusMessage(DIC_COMPLETE_OPERATION, null, null, operationNumber);
  }

  private DistributedInstanceConsensusMessage(DistributedInstanceConsensusMessageType type, long peerId, Method method, Serializable[] args, long operationNumber) {
    this.messageType = type;
    this.peerId = peerId;
    this.method = method;
    this.args = args;
    this.operationNumber = operationNumber;
  }

  public DistributedInstanceConsensusMessageType getType() {
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
