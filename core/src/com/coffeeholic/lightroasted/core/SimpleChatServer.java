/*
 * light-roasted - Java MUD server. The MIT License (MIT).
 * Copyright (c) Charlie Hsieh.
 * See LICENSE for details.
 */

package com.coffeeholic.lightroasted.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleChatServer {
  private AsynchronousServerSocketChannel assc;
  private final Set<AsynchronousSocketChannel> users = new HashSet<>();

  public static void main(String[] args) throws Exception {
    SimpleChatServer server = new SimpleChatServer();
    server.start();
    while (true) {
      Thread.sleep(5000L);
    }
  }

  private void start() throws IOException {
    ExecutorService pool = Executors.newSingleThreadExecutor();
    AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(pool);
    assc = AsynchronousServerSocketChannel.open(channelGroup);
    assc.bind(new InetSocketAddress(4000));
    assc.accept(null, new CompletionHandler<>() {

      @Override
      public void completed(AsynchronousSocketChannel asc, Object attachment) {
        assc.accept(null, this);
        try {
          asc.write(StandardCharsets.UTF_8.encode("Welcome to telnet chat server\r\n")).get();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
        ByteBuffer bb = ByteBuffer.allocate(1024);
        asc.read(bb, null, new ChatHandler(asc, bb));

        users.add(asc);
      }

      @Override
      public void failed(Throwable exc, Object attachment) {
      }
    });
  }

  private class ChatHandler implements CompletionHandler<Integer, Object> {
    private static final int BACKSPACE_KEY_CODE = 127;
    private final AsynchronousSocketChannel asc;
    private final ByteBuffer bb;
    private final MyByteArrayOutputStream baos = new MyByteArrayOutputStream();
    private final Queue<String> inputs = new LinkedList<>();

    public ChatHandler(AsynchronousSocketChannel asc, ByteBuffer bb) {
      this.asc = asc;
      this.bb = bb;
    }

    private boolean firstChar = true;

    @Override
    public void completed(Integer result, Object o) {
      if (result == -1) return;

      byte[] bytes = new byte[result];
      bb.flip().get(bytes).clear();
      for (byte b : bytes) {
        switch (b) {
          case '\n':
            if (firstChar) {
              firstChar = false;
              continue;
            }
          case '\r':
            inputs.offer(baos.toString(StandardCharsets.UTF_8));
            baos.reset();
            firstChar = true;
            continue;
          case BACKSPACE_KEY_CODE:
            baos.backspace();
            continue;
          default:
            baos.write(b);
            firstChar = false;
        }
      }

      try {
        while (!inputs.isEmpty()) {
          String message = inputs.poll() + "\r\n";
          for (AsynchronousSocketChannel user : users) {
            user.write(StandardCharsets.UTF_8.encode(message)).get();
          }
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }

      asc.read(bb, null, this);
    }

    @Override
    public void failed(Throwable throwable, Object o) {}
  }

  private static class MyByteArrayOutputStream extends ByteArrayOutputStream {
    public void backspace() {
      if (count > 0) count--;
    }
  }
}
