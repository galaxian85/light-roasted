package com.coffeeholic.lightroasted.core.commands;

public abstract class GameCommand implements Runnable {
  public abstract void run();

  abstract String[] getNames();
}
