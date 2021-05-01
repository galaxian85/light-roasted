package com.coffeeholic.lightroasted.core;

import java.util.*;
import java.util.function.Consumer;

public class PlayerManager {
  private static final PlayerManager instance = new PlayerManager();
  private final Set<Player> players;

  private PlayerManager() {
    players = Collections.synchronizedSet(new HashSet<>());
  }

  public static PlayerManager getInstance() {
    return instance;
  }

  public void addPlayer(Player player) {
    players.add(player);
  }

  public void removePlayer(Player player) {
    players.remove(player);
  }

  public void forEach(Consumer<Player> consumer) {
    players.forEach(consumer);
  }
}
