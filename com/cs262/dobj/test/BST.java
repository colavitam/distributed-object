package com.cs262.dobj.test;

import java.io.Serializable;

public interface BST<Key extends Comparable<Key> & Serializable, Value extends Serializable> extends Serializable {
  public int size();
  public boolean isEmpty();
  public Value get(Key key);
  public boolean contains(Key key);
  public void put(Key key, Value val);
  public void deleteMin();
  public void deleteMax();
  public void delete(Key key);
  public int height();
  public Key min();
  public Key max();
  public Key floor(Key key);
  public Key ceiling(Key key);
  public Key select(int k);
  public int rank(Key key);
  public Iterable<Key> keys();
  public Iterable<Key> keys(Key lo, Key hi);
  public int size(Key lo, Key hi);
}
