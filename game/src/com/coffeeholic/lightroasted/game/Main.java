/*
 * light-roasted - Java MUD server. The MIT License (MIT).
 * Copyright (c) Charlie Hsieh.
 * See LICENSE for details.
 */

package com.coffeeholic.lightroasted.game;

import com.coffeeholic.lightroasted.core.SimpleChatServer;

public class Main {
  public static void main(String[] args) throws Exception {
    SimpleChatServer chatServer = new SimpleChatServer();
    chatServer.start();
    while (true) {
      Thread.sleep(5000L);
    }
  }
}
