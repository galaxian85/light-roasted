package com.coffeeholic.lightroasted.core;

public interface Connection {
  void println(String message);
  void close();
  boolean isOpen();
}
