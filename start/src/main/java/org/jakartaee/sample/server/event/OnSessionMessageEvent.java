package org.jakartaee.sample.server.event;

import jakarta.websocket.Session;

public record OnSessionMessageEvent(Session session, String rawMessage) implements GameServerEvent {
}