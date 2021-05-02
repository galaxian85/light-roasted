package com.coffeeholic.lightroasted.game;

import com.coffeeholic.lightroasted.core.SimpleChatServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
  public static void main(String[] args) throws Exception {
    ExecutorService pool = Executors.newSingleThreadExecutor();

    SimpleChatServer chatServer = new SimpleChatServer();

    chatServer.start(pool, 4000);
    chatServer.start(pool, 5000);
    while (true) {
      Thread.sleep(5000L);
    }
  }
}
