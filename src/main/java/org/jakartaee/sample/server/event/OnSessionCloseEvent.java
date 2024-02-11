package org.jakartaee.sample.server.event;

import jakarta.websocket.Session;

public record OnSessionCloseEvent(Session session, String playerName) implements GameServerEvent {
}