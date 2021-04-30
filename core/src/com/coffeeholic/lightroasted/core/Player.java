package com.coffeeholic.lightroasted.core;

public class Player {
  private final Connection connection;

  public Player(Connection connection) {
    this.connection = connection;
  }

  public void println(String message) {
    try {
      connection.println(message);
    } catch (ConnectionException e) {
      e.printStackTrace();
    }
  }
}
