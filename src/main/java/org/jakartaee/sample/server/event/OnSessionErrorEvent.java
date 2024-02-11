package org.jakartaee.sample.server.event;

import jakarta.websocket.Session;

public record OnSessionErrorEvent(Session session, Throwable throwable) implements GameServerEvent {
}