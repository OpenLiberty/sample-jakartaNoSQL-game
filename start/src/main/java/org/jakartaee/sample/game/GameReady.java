package org.jakartaee.sample.game;

import java.util.stream.Stream;

public record GameReady(String gameId, Player playerA, Player playerB) implements GameState {

    Stream<Player> players(){
        return Stream.of(playerA, playerB);
    }
}
