package com.cs262.dobj;

import java.util.HashMap;
import java.net.Socket;
import java.io.*;

public class SocketDistributedChannel implements DistributedChannel {
  private HashMap<Long, PeerStream> peerSet;
  private MessageReceiptHandler handler;
  private long maxId;
  
  public SocketDistributedChannel() {
    this.peerSet = new HashMap<>();
    this.maxId = 0;
  }

  private PeerStream openStream(String hostname, int port) throws IOException {
    return new PeerStream(new Socket(hostname, port));
  }

  public long registerPeer(String hostname, int port) throws IOException {
    this.peerSet.put(this.maxId, openStream(hostname, port));
    return this.maxId ++;
  }

  public <T extends Serializable> void sendMessage(Message<T> message, long destination) throws IOException {
    this.peerSet.get(destination).writeObject(message);
  }

  public void registerMessageReceiptHandler(MessageReceiptHandler handler) {
    this.handler = handler;
  }

  private class PeerStream {
    private Socket sock;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public PeerStream(Socket sock) throws IOException {
      this.sock = sock;
      this.oos = new ObjectOutputStream(sock.getOutputStream());
      this.ois = new ObjectInputStream(sock.getInputStream());
    }

    public void writeObject(Serializable obj) throws IOException {
      this.oos.writeObject(obj);
    }

    public Object readObject() throws IOException, ClassNotFoundException {
      return this.ois.readObject();
    }

    public void close() throws IOException {
      this.oos.close();
      this.ois.close();
      this.sock.close();
    }
  }
}
