package com.cs262.dobj.consensus;

import com.cs262.dobj.Message;
import java.io.*;

public interface PaxosProtocol {
  public <T extends Serializable> long request(Message<T> value) throws IOException;
  public <T extends Serializable> void registerPassageHandler(PaxosPassageHandler handler);
}
