package org.jakartaee.sample.websocket.listener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jakartaee.sample.websocket.GameSessions;
import org.jakartaee.sample.websocket.event.OnSessionOpenEvent;

@ApplicationScoped
public class OnSessionOpenEventListener {

    @Inject
    GameSessions gameSessions;

    void process(@Observes OnSessionOpenEvent event) {
        gameSessions.register(event.session(), event.playerName());
    }

}
