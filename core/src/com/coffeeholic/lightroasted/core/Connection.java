package com.coffeeholic.lightroasted.core;

import java.util.concurrent.ExecutionException;

public interface Connection {
  void println(String message);
  void close();
  boolean isOpen();
}
