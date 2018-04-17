package com.cs262.dobj.consensus;

import com.cs262.dobj.Message;
import java.io.*;

public interface PaxosProtocol<T extends Serializable> {
  public long request(Message<T> value) throws IOException;
  public void registerPassageHandler(PaxosPassageHandler<T> handler);
}
