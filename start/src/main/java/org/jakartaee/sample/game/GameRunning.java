package org.jakartaee.sample.game;

import java.util.stream.Stream;

public record GameRunning(String gameId,
                          Player playerA,
                          Player playerB,
                          Movement playerAMovement,
                          Movement playerBMovement) implements GameState {
    public Stream<Player> players() {
        return Stream.of(this.playerA, this.playerB);
    }

}