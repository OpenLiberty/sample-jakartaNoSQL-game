package org.jakartaee.sample.server.event;

import jakarta.websocket.Session;

public record OnSessionOpenEvent(Session session, String playerName) implements GameServerEvent {
}