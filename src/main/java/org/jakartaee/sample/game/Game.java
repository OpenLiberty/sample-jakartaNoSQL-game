package org.jakartaee.sample.game;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;


public class Game {

    private static final Logger LOGGER = Logger.getLogger(Game.class.getName());

    private final Queue<Player> waitingRoom = new LinkedList<>();
    private final Map<Player, String> gamesByPlayer = new HashMap<>();
    private final Map<String, Set<Player>> playersByGame = new HashMap<>();
    private final Map<String, GameState> games = new HashMap<>();
    private final Set<Consumer<GameEvent>> eventListeners = new LinkedHashSet<>();

    public GameState newGame(Player player) {
        requireNonNull(player, "player is required");
        synchronized (this) {
            GameState actualGameState = getGameState(getGameId(player));
            if (actualGameState instanceof GameInvalid) {
                var playerWaiting = waitingRoom.poll();
                if (isNull(playerWaiting) || player.equals(playerWaiting)) {
                    try {
                        String gameId = getGameId(player);
                        registerPlayerOnTheGame(player, gameId);
                        return publishAndReturn(player, updateGameState(new WaitingPlayers(gameId, player)));
                    } finally {
                        waitingRoom.offer(player);
                    }
                }
                var gameId = getGameId(playerWaiting);
                registerPlayerOnTheGame(player, gameId);
                return publishAndReturn(player, updateGameState(new GameReady(gameId, playerWaiting, player)));
            }
            return actualGameState;
        }
    }

    private void registerPlayerOnTheGame(Player player, String gameId) {
        synchronized (this) {
            playersByGame.computeIfAbsent(gameId, k -> new LinkedHashSet<>()).add(player);
            gamesByPlayer.put(player, gameId);
        }
    }

    private GameState updateGameState(GameState gameState) {
        synchronized (this) {
            return games.merge(gameState.gameId(), gameState, (oldState, newState) -> newState);
        }
    }

    private String getGameId(Player player) {
        synchronized (this) {
            return gamesByPlayer.getOrDefault(player, UUID.randomUUID().toString());
        }
    }


    public GameState playGame(Player player, Movement movement) {
        requireNonNull(player, "player is required");
        requireNonNull(movement, "movement is required");
        synchronized (this) {
            GameState currentState = getGameState(getGameId(player));
            if (currentState instanceof GameInvalid) {
                return currentState;
            }
            var newState = games.computeIfPresent(currentState.gameId(), (key, oldState) -> {
                if (oldState instanceof GameReady gameReady && GamePlayers.of(gameReady.players()).isPart(player)) {
                    return publishAndReturn(player, play(gameReady, player, movement));
                }
                if (oldState instanceof GameRunning gameRunning && GamePlayers.of(gameRunning.players()).isPart(player)) {
                    return publishAndReturn(player, play(gameRunning, player, movement));
                }
                return oldState;
            });
            if (newState instanceof GameOver gameOver && GamePlayers.of(gameOver.players()).isPart(player)) {
                unregisterPlayers(gameOver);
            }
            return Optional.ofNullable(newState).orElseGet(() -> new GameInvalid(currentState.gameId()));
        }
    }

    private GameState publishAndReturn(Object source, GameState newState) {
        eventListeners().forEach(listener -> publish(listener, source, newState));
        return newState;
    }

    private void publish(Consumer<GameEvent> listener, Object source, GameState newState) {
        try {
            listener.accept(new GameEvent(source, newState));
        } catch (RuntimeException ex) {
            LOGGER.log(Level.SEVERE, "failure to process the %s by the {%s} listener: %s".formatted(newState, listener, ex.getMessage()), ex);
        }
    }

    private GameState play(GameReady gameReady, Player player, Movement movement) {
        var isPlayerA = Objects.equals(gameReady.playerA(), player);
        var isPlayerB = Objects.equals(gameReady.playerB(), player);
        if (!isPlayerA && !isPlayerB) {
            return new GameInvalid(gameReady.gameId());
        }
        return new GameRunning(gameReady.gameId(),
                gameReady.playerA(),
                gameReady.playerB(),
                isPlayerA ? movement : null,
                isPlayerB ? movement : null);
    }

    private GameState play(GameRunning gameRunning, Player player, Movement movement) {
        var isPlayerA = Objects.equals(gameRunning.playerA(), player);
        var isPlayerB = Objects.equals(gameRunning.playerB(), player);
        if (!isPlayerA && !isPlayerB) {
            return new GameInvalid(gameRunning.gameId());
        }

        var missingOpponentMovement = isPlayerA ?
                gameRunning.playerBMovement() == null
                : gameRunning.playerAMovement() == null;

        if (missingOpponentMovement) {
            return new GameRunning(gameRunning.gameId(),
                    gameRunning.playerA(),
                    gameRunning.playerB(),
                    isPlayerA ? movement : null,
                    isPlayerB ? movement : null);
        }

        return new GameOver(gameRunning.gameId(),
                gameRunning.playerA(),
                gameRunning.playerB(),
                isPlayerA ? movement : gameRunning.playerAMovement(),
                isPlayerB ? movement : gameRunning.playerBMovement());
    }


    private GameState getGameState(String gameId) {
        if (isNull(gameId)) {
            return new GameInvalid(null);
        }
        synchronized (this) {
            return Optional.ofNullable(games.get(gameId)).orElse(new GameInvalid(gameId));
        }
    }

    public GameState leaveGame(Player player) {
        if (isNull(player)) {
            return new GameInvalid(null);
        }
        synchronized (this) {
            GameState gameState = getGameState(getGameId(player));
            if (gameState instanceof GameInvalid) {
                return gameState;
            }
            Set<Player> players = unregisterPlayers(gameState);
            return publishAndReturn(player, new GameAbandoned(gameState.gameId(), players));
        }
    }

    private Set<Player> unregisterPlayers(GameState gameState) {
        synchronized (this) {
            Set<Player> players = playersByGame.remove(gameState.gameId());
            players.forEach(gamesByPlayer::remove);
            games.remove(gameState.gameId());
            players.forEach(waitingRoom::remove);
            return players;
        }
    }

    public Game addListener(Consumer<GameEvent> eventListener) {
        if (eventListener == null) return this;
        synchronized (this) {
            this.eventListeners.add(eventListener);
        }
        return this;
    }

    private Set<Consumer<GameEvent>> eventListeners() {
        synchronized (this) {
            return new LinkedHashSet<>(this.eventListeners);
        }
    }

    public boolean removeListener(Consumer<GameEvent> eventListener) {
        if (eventListener == null) return false;
        synchronized (this) {
            return this.eventListeners.remove(eventListener);
        }
    }
}