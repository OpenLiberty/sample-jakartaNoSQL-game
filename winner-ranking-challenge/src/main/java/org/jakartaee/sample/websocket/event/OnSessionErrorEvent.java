package org.jakartaee.sample.websocket.event;

import jakarta.websocket.Session;

public record OnSessionErrorEvent(Session session, Throwable throwable) implements GameServerEvent {
}