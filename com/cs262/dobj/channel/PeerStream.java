package com.cs262.dobj.channel;

import java.net.*;
import java.io.*;

// wrapper class for sockets
// serializes/deserializes objects passing through
class PeerStream {
  private Socket sock;
  private ObjectOutputStream oos;
  private ObjectInputStream ois;

  public PeerStream(Socket sock) {
    this.sock = sock;
  }

  public void writeObject(ChannelMessage obj) throws IOException {
    if (this.oos == null)
      this.oos = new ObjectOutputStream(sock.getOutputStream());

    this.oos.writeObject(obj);
  }

  public ChannelMessage readObject() throws IOException, ClassNotFoundException {
    if (this.ois == null)
      this.ois = new ObjectInputStream(sock.getInputStream());

    return (ChannelMessage) this.ois.readObject();
  }

  public void close() throws IOException {
    if (this.oos != null)
      this.oos.close();
    if (this.ois != null)
      this.ois.close();
    this.sock.close();
  }
}
