package com.cs262.dobj.consensus;

import java.io.*;
import java.lang.reflect.*;

public interface DistributedInstanceConsensusProtocol {
  public long requestOperation() throws IOException;
  public Object performOperation(Method method, Serializable[] args, long operationNumber) throws IOException;
}
