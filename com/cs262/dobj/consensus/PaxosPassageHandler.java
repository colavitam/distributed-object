package com.cs262.dobj.consensus;

import com.cs262.dobj.Message;
import java.io.*;

public interface PaxosPassageHandler<T extends Serializable> {
  public void handlePassage(long decreeNumber, Message<T> decreeValue);
}
