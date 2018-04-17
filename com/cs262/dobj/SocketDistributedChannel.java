package com.cs262.dobj;

import java.util.HashMap;
import java.net.*;
import java.io.*;

public class SocketDistributedChannel implements DistributedChannel {
  private HashMap<Long, PeerStream> peerSet;
  private MessageReceiptHandler handler;
  private long maxId;
  
  public SocketDistributedChannel(int port) throws IOException {
    this.peerSet = new HashMap<>();
    this.maxId = 0;

    spawnServer(port);
  }

  private void spawnServer(int port) throws IOException {
    ServerSocket ssock = new ServerSocket(port);

    Thread server = new Thread(() -> {
      try {
        while (true) {
          Socket sock = ssock.accept();
          long id = this.registerSocket(sock);
          this.spawnMonitor(id);
        }
      } catch(IOException e) {
        e.printStackTrace();
      }
    });

    server.start();
  }

  private PeerStream openStream(String hostname, int port) throws IOException {
    return new PeerStream(new Socket(hostname, port));
  }

  public long registerPeer(String hostname, int port) throws IOException {
    Socket sock = new Socket(hostname, port);
    long id = this.registerSocket(sock);
    this.spawnMonitor(id);
    return id;
  }

  private synchronized long registerSocket(Socket sock) throws IOException {
    this.peerSet.put(this.maxId, new PeerStream(sock));
    long id = this.maxId ++;

    return id;
  }

  public <T extends Serializable> void sendMessage(Message<T> message, long destination) throws IOException {
    this.peerSet.get(destination).writeObject(message);
  }

  public void registerMessageReceiptHandler(MessageReceiptHandler handler) {
    this.handler = handler;
  }

  private void spawnMonitor(long id) {
    Thread inMonitor = new Thread(() -> {
      PeerStream stream = peerSet.get(id);

      try {
        while(true) {
          Object obj = stream.readObject();
          dispatchReceipt((Message) obj, id);
        }
      } catch(IOException e) {
        e.printStackTrace();
      } catch(ClassNotFoundException cnf) {
        cnf.printStackTrace();
      } finally {
        try {
          stream.close();
        } catch(Exception e) {
        }
      }
    });

    inMonitor.start();
  }

  private synchronized <T extends Serializable> void dispatchReceipt(Message<T> message, long src) {
    if (handler != null) {
      synchronized (handler) {
        handler.handleMessageReceipt(message, src);
      }
    }
  }

  private class PeerStream {
    private Socket sock;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public PeerStream(Socket sock) throws IOException {
      this.sock = sock;
    }

    public void writeObject(Serializable obj) throws IOException {
      if (this.oos == null)
        this.oos = new ObjectOutputStream(sock.getOutputStream());

      this.oos.writeObject(obj);
    }

    public Object readObject() throws IOException, ClassNotFoundException {
      if (this.ois == null)
        this.ois = new ObjectInputStream(sock.getInputStream());

      return this.ois.readObject();
    }

    public void close() throws IOException {
      if (this.oos != null)
        this.oos.close();
      if (this.ois != null)
        this.ois.close();
      this.sock.close();
    }
  }
}
