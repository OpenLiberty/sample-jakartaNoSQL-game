package org.jakartaee.sample.game;

public sealed interface GameState permits
        WaitingPlayers,
        GameInvalid,
        GameAbandoned,
        GameReady,
        GameRunning,
        GameOver {
    String gameId();
}