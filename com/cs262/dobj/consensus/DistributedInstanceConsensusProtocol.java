package com.cs262.dobj.consensus;

import java.io.*;
import java.lang.reflect.*;

public interface DistributedInstanceConsensusProtocol {
  public long requestOperation(boolean atomicGroup) throws IOException;
  public void completeOperation();
  public Object performOperation(Method method, Serializable[] args, long operationNumber) throws IOException;
  public void terminateAtomicGroup() throws IOException;
}
