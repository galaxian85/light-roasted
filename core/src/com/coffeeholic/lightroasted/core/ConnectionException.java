/*
 * light-roasted - Java MUD server. The MIT License (MIT).
 * Copyright (c) Charlie Hsieh.
 * See LICENSE for details.
 */

package com.coffeeholic.lightroasted.core;

public class ConnectionException extends RuntimeException {

  public ConnectionException(Throwable cause) {
    super(cause);
  }
}
