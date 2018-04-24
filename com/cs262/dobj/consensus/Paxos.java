package com.cs262.dobj.consensus;

import java.io.*;
import java.util.*;

public class Paxos implements ConsensusProtocol {
  private DistributedChannel ch;

  private ConsensusPassageHandler handler;
  private ArrayList<Message> ledger;

  public Paxos(DistributedChannel ch) {
    this.ch = ch;
  }

  public <T extends Serializable> long propose(Message<T> decree) throws IOException {
    
  }

  public void registerPassageHandler(ConsensusPassageHandler handler) {
    this.handler = handler;
  }
}
