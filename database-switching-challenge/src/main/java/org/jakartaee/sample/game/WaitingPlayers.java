package org.jakartaee.sample.game;

public record WaitingPlayers(String gameId, Player player) implements GameState {
}
