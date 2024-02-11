package org.jakartaee.sample.server;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import org.jakartaee.sample.server.event.GameServerEvent;
import org.jakartaee.sample.server.event.OnSessionCloseEvent;
import org.jakartaee.sample.server.event.OnSessionErrorEvent;
import org.jakartaee.sample.server.event.OnSessionMessageEvent;
import org.jakartaee.sample.server.event.OnSessionOpenEvent;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint("/jnopo/{playerName}")
@ApplicationScoped
public class GameServer {

    private static final Logger LOGGER = Logger.getLogger(GameServer.class.getName());

    private Event<GameServerEvent> event;

    @Inject
    public GameServer(Event<GameServerEvent> event) {
        this.event = event;
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("playerName") String playerName)
    {
        LOGGER.log(Level.INFO, "Create new session with %s".formatted(playerName));
        event.fire(new OnSessionOpenEvent(session, URLDecoder.decode(playerName, StandardCharsets.UTF_8)));
    }

    @OnClose
    public void onClose(Session session, @PathParam("playerName") String playerName) {
        LOGGER.log(Level.INFO, "Session with %s closed".formatted(playerName));
        event.fire(new OnSessionCloseEvent(session, URLDecoder.decode(playerName, StandardCharsets.UTF_8)));
    }

    @OnMessage
    public void onMessage(String rawMessage, Session session) {
        LOGGER.log(Level.INFO, "onMessage: %s".formatted(rawMessage));
        event.fire(new OnSessionMessageEvent(session, rawMessage));
    }

    @OnError
    public void onError(Session session, @PathParam("playerName") String playerName, Throwable throwable) {
        LOGGER.log(Level.SEVERE, "onError", throwable);
        event.fire(new OnSessionErrorEvent(session, throwable));
    }

}
