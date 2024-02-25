package org.jakartaee.sample.server;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.websocket.Session;
import org.jakartaee.sample.game.Game;
import org.jakartaee.sample.game.GameAbandoned;
import org.jakartaee.sample.game.GameEvent;
import org.jakartaee.sample.game.GameInvalid;
import org.jakartaee.sample.game.GameOver;
import org.jakartaee.sample.game.GamePlayers;
import org.jakartaee.sample.game.GameReady;
import org.jakartaee.sample.game.GameRunning;
import org.jakartaee.sample.game.GameState;
import org.jakartaee.sample.game.Movement;
import org.jakartaee.sample.game.Player;
import org.jakartaee.sample.game.WaitingPlayers;
import org.jakartaee.sample.server.message.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static org.jakartaee.sample.server.message.MessageField.gameId;
import static org.jakartaee.sample.server.message.MessageField.movement;
import static org.jakartaee.sample.server.message.MessageField.opponentMovement;
import static org.jakartaee.sample.server.message.MessageField.opponentName;
import static org.jakartaee.sample.server.message.MessageType.CONNECTED;
import static org.jakartaee.sample.server.message.MessageType.GAME_INVALID;
import static org.jakartaee.sample.server.message.MessageType.GAME_OVER_ABANDONED;
import static org.jakartaee.sample.server.message.MessageType.GAME_OVER_DRAW;
import static org.jakartaee.sample.server.message.MessageType.GAME_OVER_YOU_LOSE;
import static org.jakartaee.sample.server.message.MessageType.GAME_OVER_YOU_WIN;
import static org.jakartaee.sample.server.message.MessageType.GAME_READY;
import static org.jakartaee.sample.server.message.MessageType.GAME_RUNNING;
import static org.jakartaee.sample.server.message.MessageType.WAITING_PLAYERS;

@ApplicationScoped
public class GameSessions {

    private static final Logger LOG = Logger.getLogger(GameSessions.class.getName());

    private final Map<String, Session> sessionsById = new HashMap<>();

    private final Map<String, Player> playersBySessionId = new HashMap<>();

    private final Game game = new Game();

    private Event<GameEvent> event;

    @Inject
    public GameSessions(Event<GameEvent> event) {
        this.event = event;
    }

    /**
     * do not use it, it is required by CDI
     */
    @Deprecated
    public GameSessions() {
    }

    @PostConstruct
    void postConstruct() {
        game.addListener(this::onGameEvent)
                .addListener(event::fire);
    }

    private Optional<Player> getPlayerBySession(Session session) {
        return getPlayerBySessionId(session.getId());
    }

    private Optional<Player> getPlayerBySessionId(String sessionId) {
        synchronized (this) {
            return Optional.ofNullable(playersBySessionId.get(sessionId));
        }
    }

    private Optional<Session> getSessionById(String sessionId) {
        synchronized (this) {
            return Optional.ofNullable(sessionsById.get(sessionId));
        }
    }

    public void register(Session session, String playerName) {
        synchronized (this) {
            String sessionId = session.getId();
            sessionsById.put(sessionId, session);
            playersBySessionId.put(sessionId, Player.of(sessionId, playerName));
        }
        send(session, CONNECTED.build());
    }

    public void unregister(Session session) {

        synchronized (this) {
            String sessionId = session.getId();
            sessionsById.remove(sessionId, session);
            leaveGame(Optional.ofNullable(playersBySessionId.remove(sessionId)), session);
        }
    }

    private void leaveGame(Optional<Player> playerRef, Session session) {
        playerRef
                .ifPresent(game::leaveGame);
    }

    public void playerWantsNewGame(Message message, Session session) {
        getPlayerBySession(session)
                .ifPresent(game::newGame);
    }

    public void playerWantsToPlay(Message message, Session session) {
        getPlayerBySession(session)
                .ifPresent(player -> movement.get(message)
                        .map(Movement::valueOf)
                        .ifPresent(playerMovement -> game.playGame(player, playerMovement)));
    }

    private void onGameEvent(GameEvent event) {

        var gameState = event.gameState();

        if (gameState instanceof GameInvalid gameInvalid
                && event.source() instanceof Player player) {
            getSessionById(player.id())
                    .ifPresent(session -> send(session,gameInvalid));
        }

        if (gameState instanceof WaitingPlayers waitingPlayers) {
            getSessionById(waitingPlayers.player().id())
                    .ifPresent(session -> send(session, waitingPlayers));
        }

        if (gameState instanceof GameReady gameReady) {
            Stream.of(gameReady.playerA().id(), gameReady.playerB().id())
                    .map(this::getSessionById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(playerSession -> send(playerSession, gameReady));
        }

        if (gameState instanceof GameRunning gameRunning) {
            Stream.of(gameRunning.playerA().id(), gameRunning.playerB().id())
                    .map(this::getSessionById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(playerSession -> send(playerSession, gameRunning));
        }

        if (gameState instanceof GameOver gameOver && gameOver.isTied()) {
            sendTiedGame(gameOver);
        }

        if (gameState instanceof GameOver gameOver && !gameOver.isTied()) {

            Stream.of(gameOver.playerA().id(), gameOver.playerB().id())
                    .map(this::getSessionById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(playerSession -> send(playerSession, gameOver));
        }

        if (gameState instanceof GameAbandoned gameAbandoned
                && event.source() instanceof Player player) {
            GamePlayers.of(gameAbandoned.players())
                    .getOpponent(player)
                    .flatMap(opponent -> getSessionById(opponent.id()))
                    .ifPresent(opponentSession -> send(opponentSession, player, gameAbandoned));
        }

    }

    private void send(Session session, WaitingPlayers waitingPlayers) {
        Message message = WAITING_PLAYERS.build(m -> m.set(gameId, waitingPlayers.gameId()));
        send(session, message);
    }

    private void send(Session session, GameReady gameReady) {
        getPlayerBySession(session)
                .ifPresent(player -> {
                    GamePlayers gamePlayers = GamePlayers.of(gameReady);
                    gamePlayers.getOpponent(player)
                            .ifPresent(opponent -> {
                                Message message = GAME_READY
                                        .build(s -> s.set(gameId, gameReady.gameId())
                                                .set(opponentName, opponent.name()));
                                send(session, message);
                            });
                });
    }

    private void send(Session session, GameRunning gameRunning) {
        getPlayerBySession(session)
                .ifPresent(player -> {
                    GamePlayers gamePlayers = GamePlayers.of(gameRunning);
                    gamePlayers.getOpponent(player)
                            .ifPresent(opponent -> {
                                Message message = GAME_RUNNING
                                        .build(s -> s.set(gameId, gameRunning.gameId())
                                                .set(opponentName, opponent.name()));
                                send(session, message);
                            });
                });
    }

    private void send(Session session, GameOver gameOver) {
        var isPlayerA = gameOver.playerA().id().equals(session.getId());
        var isWinner = session.getId().equals(gameOver.winner().orElseThrow().id());
        send(session, (isWinner ? GAME_OVER_YOU_WIN : GAME_OVER_YOU_LOSE)
                .build(s -> s.set(gameId, gameOver.gameId())
                        .set(opponentName, (isPlayerA ? gameOver.playerB() : gameOver.playerA()).name())
                        .set(opponentMovement, (isPlayerA ? gameOver.playerBMovement() : gameOver.playerAMovement()).name())));
    }

    private void sendTiedGame(GameOver gameOver) {
        getSessionById(gameOver.playerA().id())
                .ifPresent(sessionPlayerA ->
                        send(sessionPlayerA, GAME_OVER_DRAW.build(s -> s.set(gameId, gameOver.gameId())
                                .set(opponentName, gameOver.playerB().name())
                                .set(opponentMovement, gameOver.playerBMovement().name()))));

        getSessionById(gameOver.playerB().id())
                .ifPresent(sessionPlayerB ->
                        send(sessionPlayerB, GAME_OVER_DRAW.build(s -> s.set(gameId, gameOver.gameId())
                                .set(opponentName, gameOver.playerA().name())
                                .set(opponentMovement, gameOver.playerAMovement().name()))));
    }

    private void send(Session session, Player whoAbandoned, GameAbandoned gameAbandoned) {
        Message message = GAME_OVER_ABANDONED.build(m ->
                m.set(gameId, gameAbandoned.gameId())
                        .set(opponentName, whoAbandoned.name()));
        send(session, message);
    }

    private void send(Session session, GameInvalid gameInvalid) {
        send(session, GAME_INVALID.build(s -> s.set(gameId, gameInvalid.gameId())));
    }

    private void send(Session session, Message message) {
        if (session.isOpen()) {
            session.getAsyncRemote().sendText(message.toJson());
        } else {
            unregister(session);
        }
    }

    public void receivedUnsupportedMessage(Message message, Session session) {
        LOG.log(Level.SEVERE, "received an unsupported message: %s".formatted(message.toJson()));
    }

    public void receivedInvalidMessage(String rawMessage, Session session) {
        LOG.log(Level.WARNING, "received an invalid message: %s".formatted(rawMessage));
    }
}
