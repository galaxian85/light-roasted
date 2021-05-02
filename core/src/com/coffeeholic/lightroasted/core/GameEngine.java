package com.coffeeholic.lightroasted.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class GameEngine {
  private final GameEngine instance = new GameEngine();
  private final BlockingQueue<Runnable> commands;

  private GameEngine() {
    commands = new LinkedBlockingQueue<>();
  }

  public GameEngine getInstance() {
    return instance;
  }

  public void start() {
    while (true) {
      try {
        commands.take().run();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
