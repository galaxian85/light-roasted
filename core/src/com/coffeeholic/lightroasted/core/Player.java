/*
 * light-roasted - Java MUD server. The MIT License (MIT).
 * Copyright (c) Charlie Hsieh.
 * See LICENSE for details.
 */

package com.coffeeholic.lightroasted.core;

public class Player {
  private final Connection connection;
  private String accountName;

  public String getAccountName() {
    return accountName;
  }

  public void setAccountName(String accountName) {
    this.accountName = accountName;
  }

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
