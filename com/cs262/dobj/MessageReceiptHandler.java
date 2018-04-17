package com.cs262.dobj;

import java.io.*;

public interface MessageReceiptHandler {
  public <T extends Serializable> void handleMessageReceipt(Message<T> message, long src);
}
