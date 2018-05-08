package com.cs262.dobj.consensus;

import java.util.HashMap;
import java.net.*;
import java.io.*;

/*
 * A DistributedObjectChannel is a single shared communication context.
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
public class DistributedObjectChannel {
  private DistributedObjectConsensus consensus; // parent consensus instance TODO factor out interface
  private HashMap<Long, PeerStream> peerSet; // set of peers
  public final long id; // this process's ID on the channel

  /* create a new channel; act as a server on the given port */
  public DistributedObjectChannel(DistributedObjectConsensus consensus, int serverPort) throws IOException {
    this.consensus = consensus;
    this.peerSet = new HashMap<>();
    this.id = 0;

    // spawn local server
    spawnServer(serverPort);
  }

  /* create a new channel; act as a server on the given port */
  public DistributedObjectChannel(DistributedObjectConsensus consensus, String hostName, int hostPort, String serverName, int serverPort, long myId) throws IOException, ClassNotFoundException, Exception {
    this.consensus = consensus;
    this.peerSet = new HashMap<>();
    this.id = myId;

    // attempt to connect to supplied extant channel server
    PeerStream host = new PeerStream(new Socket(hostName, hostPort));

    // join handshake: send a JoinRequestMessage and get a WelcomeMessage back
    host.writeObject(new JoinRequestMessage(myId, serverName, serverPort));

    ChannelMessage m = host.readObject();
    if (m instanceof WelcomeMessage) {
      WelcomeMessage wm = (WelcomeMessage) m;
      peerSet.put(wm.src, host);
      consensus.setState(wm.state);
    } else {
      throw new Exception("response from hose was not a WelcomeMessage");
    }

    // spawn local server
    spawnServer(serverPort);
  }

  private void spawnServer(int port) throws IOException {
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
    stream.writeObject(new ConnectionMessage(id));

    putPeer(peerId, stream);
    spawnMonitor(peerId);
  }

  // fork a thread to perform the peer registration consensus dance
  private void registerNewPeer(Socket sock) {
    Thread registrar = new Thread(() -> {
      PeerStream stream = new PeerStream(sock);

      try {
        ChannelMessage obj = stream.readObject();
        if (obj instanceof JoinRequestMessage) {
          // new peer, so register its joining
          JoinRequestMessage jrm = (JoinRequestMessage) obj;
          consensus.registerJoin(jrm.src, jrm.name, jrm.port); // TODO ID assignments

          putPeer(jrm.src, stream);
          stream.writeObject(new WelcomeMessage(id, consensus.getState()));
          spawnMonitor(jrm.src);
        } else if (obj instanceof ConnectionMessage) {
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
  }

  public void sendMessage(ChannelMessage message, long destination) throws IOException {
    getPeer(destination).writeObject(message);
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
    if (message instanceof ConsensusMessage) {
      ConsensusMessage cm = (ConsensusMessage) message;
      consensus.processMessage(id, cm.mesg);
    } else {
      throw new Exception("received bad message");
    }
  }
}
