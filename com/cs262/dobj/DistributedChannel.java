package com.cs262.dobj;

import java.util.HashMap;
import java.net.*;
import java.io.*;

/*
 * A DistributedChannel is a single shared communication context.
 *
 * An instance provides the means to send and receive messages to and from a
 * pool of participating processes.
 *
 * Message transmission is subject to only intactness guarantees: messages may
 * otherwise be delayed arbitrarily, duplicated, or lost.
 * But currently we form sockets with all peers, so at least we won't lose
 * messages without noticing.
 * However, this also means that all participants in the network have to be
 * publicly addressable.
 *
 * Constructors take the parent DistributedContext as an argument so we know
 * where to route incoming messages.
 */
// TODO make abstract class to generalize join request upcalls?
public class DistributedObjectChannel {
  private HashMap<Long, PeerStream> peerSet; // set of peers
  private DistributedContext context; // context for handling messages and incoming connections
  public final long id; // this process's ID on the channel

  /* create a new channel; act as a server on the given port */
  public DistributedObjectChannel(DistributedContext ctx, int port) throws IOException {
    this.context = ctx;
    this.peerSet = new HashMap<>();
    this.id = 0;

    // spawn local server
    spawnServer(port);
  }

  /* create a new channel; act as a server on the given port */
  public DistributedObjectChannel(DistributedContext ctx, int port, String hostName, int hostPort) throws IOException {
    this.context = ctx;
    this.peerSet = new HashMap<>();

    // attempt to connect to supplied extant channel server
    PeerStream host = new PeerStream(new Socket(hostName, hostPort));
    Message m = host.readObject();
    if (m instanceof WelcomeMessage) {
      WelcomeMessage wm = (WelcomeMessage) m;
      id = wm.yourId;
      peerSet.put(wm.myId, host);
      context.doWelcome(wm);
    }

    // spawn local server
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

  private PeerStream openStream(String hostName, int port) throws IOException {
    return new PeerStream(new Socket(hostName, port));
  }

  // TODO 
  // public void registerPeer(long peerNumber) throws IOException;
  public long registerPeer(String hostName, int port) throws IOException {
    Socket sock = new Socket(hostName, port);
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

  public <T extends Serializable> void broadcastMessage(Message<T> message) throws IOException {
    for (PeerStream peer : peerSet.values()) {
      peer.writeObject(message);
    }
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

  private synchronized <T extends Serializable> void dispatchReceipt(Message message) {
    synchronized (context) {
      context.receivedMessage(message);
    }
  }

  // wrapper class for sockets
  // serializes/deserializes objects passing through
  private class PeerStream {
    private Socket sock;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public PeerStream(Socket sock) throws IOException {
      this.sock = sock;
    }

    public void writeObject(Message obj) throws IOException {
      if (this.oos == null)
        this.oos = new ObjectOutputStream(sock.getOutputStream());

      this.oos.writeObject(obj);
    }

    public Message readObject() throws IOException, ClassNotFoundException {
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

  // class for coordination message when joining a channel
  private class PeerMessage {
    public final myId;
    public final yourId;

    public PeerMessage(long myId, long yourId) {
      this.myId = myId;
      this.yourId = yourId;
    }
  }
}
