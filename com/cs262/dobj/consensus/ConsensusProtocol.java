package com.cs262.dobj.consensus;

import com.cs262.dobj.Message;
import java.io.*;

// interface for a consensus protocol
public interface ConsensusProtocol {
  // propose a decree containing some arbitrary information for consensus
  // returns once:
  //  - consensus has been achieved
  //  - all preceding operations have been received
  public <T extends Serializable> long propose(Message<T> decree) throws IOException;

  // handler for the passage of a decree
  // will only trigger once all preceding decrees have been received
  public <T extends Serializable> void registerPassageHandler(ConsensusPassageHandler handler);
}
