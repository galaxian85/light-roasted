package com.coffeeholic.lightroasted.core.commands;

import java.util.Optional;

public class CommandLoader {
  // TODO not really implemented yet
  public Optional<GameCommand> getCommand(String str) {
    if ("chat".equals(str))
      return Optional.of(new Chat());

    return Optional.empty();
  }
}
