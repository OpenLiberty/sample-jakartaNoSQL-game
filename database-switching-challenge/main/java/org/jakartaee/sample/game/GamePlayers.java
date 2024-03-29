package org.jakartaee.sample.game;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record GamePlayers(Set<Player> players) {
    public boolean isPart(Player player) {
        return players().stream()
                .anyMatch(p -> Objects.equals(p, player));
    }

    public Optional<Player> getOpponent(Player player) {
        if (!isPart(player))
            return Optional.empty();
        return players().stream()
                .filter(p -> !Objects.equals(p, player))
                .findFirst();
    }

    public static GamePlayers of(Player... players) {
        return new GamePlayers(Set.of(players));
    }

    public static GamePlayers of(Collection<Player> players) {
        return new GamePlayers(new HashSet<>(players));
    }

    public static GamePlayers of(Stream<Player> players) {
        return new GamePlayers(players.collect(Collectors.toSet()));
    }

    public static GamePlayers of(GameState gameState) {

        return switch (gameState) {

            case WaitingPlayers waitingPlayers -> GamePlayers.of(Set.of(waitingPlayers.player()));

            case GameReady gameReady -> GamePlayers.of(gameReady.players());

            case GameRunning gameRunning -> GamePlayers.of(gameRunning.players());

            case GameOver gameOver -> GamePlayers.of(gameOver.players());

            case GameAbandoned gameAbandoned -> GamePlayers.of(gameAbandoned.players());

            default -> GamePlayers.of();

        };
    }
}
