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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleChatServer {
  private AsynchronousServerSocketChannel assc;
  private final Set<Player> players = new HashSet<>();

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

        ByteBuffer bb = ByteBuffer.allocate(1024);
        Player player = new Player(new ASCConnection(asc));
        player.println("Welcome to telnet chat server");
        asc.read(bb, null, new ChatHandler(asc, bb, player));

        players.add(player);
      }

      @Override
      public void failed(Throwable exc, Object attachment) {}
    });
  }

  private class ChatHandler implements CompletionHandler<Integer, Object> {
    private final AsynchronousSocketChannel asc;
    private final Player player;
    private final ByteBuffer bb;
    private final BackspaceByteArrayOutputStream baos = new BackspaceByteArrayOutputStream();
    private final Queue<String> inputs = new LinkedList<>();

    public ChatHandler(AsynchronousSocketChannel asc, ByteBuffer bb, Player player) {
      this.asc = asc;
      this.bb = bb;
      this.player = player;
    }

    private boolean firstChar = true;

    @Override
    public void completed(Integer result, Object attachment) {
      if (result == -1) { // disconnected
        players.remove(player);
        return;
      }

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
          case KeyCode.BACKSPACE:
            baos.backspace();
            continue;
          default:
            baos.write(b);
            firstChar = false;
        }
      }

      while (!inputs.isEmpty()) {
        String message = inputs.poll();
        for (Player p : players) {
          p.println(message);
        }
      }

      asc.read(bb, null, this);
    }

    @Override
    public void failed(Throwable throwable, Object attachment) {}
  }

  private static class BackspaceByteArrayOutputStream extends ByteArrayOutputStream {
    public void backspace() {
      if (count > 0) count--;
    }
  }
}
