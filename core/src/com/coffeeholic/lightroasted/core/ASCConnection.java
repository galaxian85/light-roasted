/*
 * light-roasted - Java MUD server. The MIT License (MIT).
 * Copyright (c) Charlie Hsieh.
 * See LICENSE for details.
 */

package com.coffeeholic.lightroasted.core;

import java.io.IOException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;

public class ASCConnection implements Connection {
  private final AsynchronousSocketChannel asc;

  public ASCConnection(AsynchronousSocketChannel asc) {
    this.asc = asc;
  }

  @Override
  public void println(String message) {
    try {
      asc.write(StandardCharsets.UTF_8.encode(message + KeyCode.CRLF)).get();
    } catch (InterruptedException | ExecutionException e) {
      close();
      throw new ConnectionException(e);
    }
  }

  @Override
  public void close() {
    try {
      asc.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isOpen() {
    return asc.isOpen();
  }
}
