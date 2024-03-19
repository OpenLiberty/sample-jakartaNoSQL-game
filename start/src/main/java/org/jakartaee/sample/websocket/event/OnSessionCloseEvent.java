package org.jakartaee.sample.websocket.event;

import jakarta.websocket.Session;

public record OnSessionCloseEvent(Session session, String playerName) implements GameServerEvent {
}