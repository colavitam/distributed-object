
public interface PaxosPassageHandler<T extends Serializable> {
  public void handlePassage(long decreeNumber, Message<T> decreeValue);
}
