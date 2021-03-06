package com.cs262.dobj.channel;

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
// TODO should this just be an inner class
public class DistributedChannel<State extends Serializable, Message extends Serializable> {
  private DistributedChannelHandler<State, Message> handler; // parent message handler
  private HashMap<Long, PeerStream> peerSet; // set of peers
  private long id; // this process's ID on the channel

  /* create a new channel; act as a server on the given port */
  public DistributedChannel(DistributedChannelHandler<State, Message> handler, int serverPort, State startState) throws IOException {
    this.handler = handler;
    this.peerSet = new HashMap<>();
    this.id = 0;

    handler.setState(startState);
  }

  /* create a new channel; act as a server on the given port */
  public DistributedChannel(DistributedChannelHandler<State, Message> handler, String hostName, int hostPort, String serverName, int serverPort, long myId) throws IOException, ClassNotFoundException, Exception {
    this.handler = handler;
    this.peerSet = new HashMap<>();
    this.id = myId;

    // attempt to connect to supplied extant channel server
    PeerStream host = new PeerStream(new Socket(hostName, hostPort));

    // join handshake: send a JoinRequestMessage and get a WelcomeMessage back
    host.writeObject(new RegisterMessage(myId, serverName, serverPort));

    ChannelMessage m = host.readObject();
    if (m instanceof WelcomeMessage<?>) {
      WelcomeMessage<State> wm = (WelcomeMessage<State>) m;
      peerSet.put(wm.src, host);
      handler.setState(wm.state);
    } else {
      throw new Exception("response from host was not a WelcomeMessage");
    }
  }

  public long getId() {
    return id;
  }

  public void spawnServer(int port) throws IOException {
    ServerSocket ssock = new ServerSocket(port);

    Thread server = new Thread(() -> {
      try {
        while (true) {
          Socket sock = ssock.accept();
          registerNewPeer(sock);
        }
      } catch(IOException e) {
        e.printStackTrace();
      }
    });

    server.start();
  }

  private synchronized void putPeer(long peerId, PeerStream stream) {
    peerSet.put(peerId, stream);
  }

  private synchronized PeerStream getPeer(long peerId) {
    return peerSet.get(peerId);
  }

  public void registerPeer(long peerId, String peerName, int peerPort) throws IOException {
    PeerStream stream = new PeerStream(new Socket(peerName, peerPort));
    stream.writeObject(new ConnectMessage(id));

    putPeer(peerId, stream);
    spawnMonitor(peerId);
  }

  // fork a thread to perform the peer registration consensus dance
  private void registerNewPeer(Socket sock) {
    Thread registrar = new Thread(() -> {
      PeerStream stream = new PeerStream(sock);

      try {
        ChannelMessage obj = stream.readObject();
        if (obj instanceof RegisterMessage) {
          // new peer, so register its joining
          RegisterMessage rm = (RegisterMessage) obj;

          handler.registerPeer(rm.src, rm.name, rm.port); // TODO ID assignments
          putPeer(rm.src, stream);

          WelcomeMessage<State> wm = new WelcomeMessage<>(id, handler.getState());
          stream.writeObject(wm);

          spawnMonitor(rm.src);
        } else if (obj instanceof ConnectMessage) {
          putPeer(obj.src, stream);

          spawnMonitor(obj.src);
        }
        System.err.println("newly connected peer did not perform handshake");
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    });

    registrar.start();
  }

  public synchronized void sendMessage(long destination, Message message) {
    Thread sender = new Thread(() -> {
      ProtocolMessage<Message> pm = new ProtocolMessage<>(id, message);
      try {
        getPeer(destination).writeObject(pm);
      } catch (IOException e) {
        e.printStackTrace();
      }
    });

    sender.start();
  }

  private synchronized void spawnMonitor(long id) {
    Thread inMonitor = new Thread(() -> {
      PeerStream stream = getPeer(id);

      try {
        while (true) {
          ChannelMessage obj = stream.readObject();
          assert obj.src == id : "received message with inconsistent sender ID";
          dispatchReceipt(id, obj);
        }
      } catch (IOException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException cnf) {
        cnf.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        try {
          stream.close();
        } catch(Exception e) {
        }
      }
    });

    inMonitor.start();
  }

  private void dispatchReceipt(long id, ChannelMessage message) throws Exception {
    if (message instanceof ProtocolMessage<?>) {
      ProtocolMessage<Message> pm = (ProtocolMessage<Message>) message;
      handler.processMessage(id, pm.mesg);
    } else {
      throw new Exception("received bad message");
    }
  }
}
