package org.jakartaee.sample.websocket.listener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jakartaee.sample.websocket.GameSessions;
import org.jakartaee.sample.websocket.event.OnSessionCloseEvent;

@ApplicationScoped
public class OnSessionCloseEventListener {

    @Inject
    GameSessions gameSessions;

    void process(@Observes OnSessionCloseEvent event) {
        gameSessions.unregister(event.session());
    }

}
