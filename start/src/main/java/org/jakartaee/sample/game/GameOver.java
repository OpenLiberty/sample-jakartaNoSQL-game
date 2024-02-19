package org.jakartaee.sample.game;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public record GameOver(String gameId,
                       Player playerA,
                       Player playerB,
                       Movement playerAMovement,
                       Movement playerBMovement) implements GameState {

    public GameOver {
        Objects.requireNonNull(gameId, "game id is required");
        Objects.requireNonNull(playerA, "player 'A' is required");
        Objects.requireNonNull(playerB, "player 'B' is required");
        Objects.requireNonNull(playerAMovement, "player 'A' movement is required");
        Objects.requireNonNull(playerBMovement, "player 'B' movement is required");
    }

    public boolean isTied() {
        return playerAMovement.equals(playerBMovement);
    }

    public Stream<Player> players() {
        return Stream.of(playerA, playerB);
    }

    public Optional<Player> winner() {
        if (isTied()) {
            return Optional.empty();
        }

        return Optional
                .of(playerAMovement.beats(playerBMovement))
                .filter(Boolean.TRUE::equals)
                .flatMap(v -> Optional.of(playerA))
                .or(() -> Optional.of(playerB));
    }

    public Optional<Player> loser() {
        if (isTied()) {
            return Optional.empty();
        }
        return Optional
                .of(playerAMovement.beats(playerBMovement))
                .filter(Boolean.TRUE::equals)
                .flatMap((v) -> Optional.of(playerB))
                .or(() -> Optional.of(playerA));
    }

    public Optional<Movement> winnerMovement() {
        if (isTied()) {
            return Optional.empty();
        }
        return Optional
                .of(playerAMovement.beats(playerBMovement))
                .filter(Boolean.TRUE::equals)
                .flatMap((v) -> Optional.of(playerAMovement))
                .or(() -> Optional.of(playerBMovement));
    }

    public Optional<Movement> loserMovement() {
        if (isTied()) {
            return Optional.empty();
        }
        return Optional
                .of(playerAMovement.beats(playerBMovement))
                .filter(Boolean.TRUE::equals)
                .flatMap((v) -> Optional.of(playerBMovement))
                .or(() -> Optional.of(playerAMovement));
    }

    public Optional<LoserInfo> loserInfo() {
        if (isTied()) {
            return Optional.empty();
        }
        return Optional.of(new LoserInfo(this.gameId, this.loser().orElse(null), this.loserMovement().orElse(null)));
    }

    public Optional<WinnerInfo> winnerInfo() {
        if (isTied()) {
            return Optional.empty();
        }
        return Optional.of(new WinnerInfo(this.gameId, this.winner().orElse(null), this.winnerMovement().orElse(null)));
    }

    public PlayerInfo playerAInfo() {
        return new PlayerInfo(this.gameId, this.playerA, this.playerAMovement);
    }

    public PlayerInfo playerBInfo() {
        return new PlayerInfo(this.gameId, this.playerB, this.playerBMovement);
    }

    public static record LoserInfo(String gameId, Player player, Movement movement) {
    }

    public static record PlayerInfo(String gameId, Player player, Movement movement) {
    }

    public static record WinnerInfo(String gameId, Player player, Movement movement) {
    }
}
