package org.jakartaee.sample.game;

import java.util.Set;

public record GameAbandoned(String gameId, Set<Player> players) implements GameState {

}
