package org.jakartaee.sample.websocket.event;

import jakarta.websocket.Session;

public record OnSessionMessageEvent(Session session, String rawMessage) implements GameServerEvent {
}