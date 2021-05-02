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
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class SimpleChatServer {

  public void start(ExecutorService threadPool, int port) throws IOException {
    AsynchronousChannelGroup channelGroup = AsynchronousChannelGroup.withThreadPool(threadPool);
    AsynchronousServerSocketChannel assc = AsynchronousServerSocketChannel.open(channelGroup);
    assc.bind(new InetSocketAddress(port));
    assc.accept(assc, new CompletionHandler<>() {

      @Override
      public void completed(AsynchronousSocketChannel asc, AsynchronousServerSocketChannel assc) {
        assc.accept(assc, this);

        ByteBuffer bb = ByteBuffer.allocate(1024);
        Player player = new Player(new ASCConnection(asc));
        player.println("Welcome to telnet chat server");
        asc.read(bb, null, new ChatHandler(asc, bb, player));

        PlayerManager.getInstance().addPlayer(player);
      }

      @Override
      public void failed(Throwable exc, AsynchronousServerSocketChannel assc) {
        throw new RuntimeException(exc);
      }
    });
  }

  private static class ChatHandler implements CompletionHandler<Integer, Object> {
    private final AsynchronousSocketChannel asc;
    private final Player player;
    private final ByteBuffer bb;
    private final BackspaceByteArrayOutputStream baos = new BackspaceByteArrayOutputStream();
    private final Queue<String> inputs = new LinkedList<>();
    private final PlayerManager pm = PlayerManager.getInstance();

    public ChatHandler(AsynchronousSocketChannel asc, ByteBuffer bb, Player player) {
      this.asc = asc;
      this.bb = bb;
      this.player = player;
    }

    private boolean firstChar = true;

    @Override
    public void completed(Integer result, Object attachment) {
      if (result == -1) { // disconnected
        pm.removePlayer(player);
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
        pm.forEach(p -> p.println(message));
      }

      asc.read(bb, null, this);
    }

    @Override
    public void failed(Throwable exc, Object attachment) {
      throw new RuntimeException(exc);
    }
  }

  private static class BackspaceByteArrayOutputStream extends ByteArrayOutputStream {
    public void backspace() {
      if (count > 0) count--;
    }
  }
}
