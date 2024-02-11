package org.jakartaee.sample.server.listener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jakartaee.sample.server.GameSessions;
import org.jakartaee.sample.server.event.OnSessionOpenEvent;

@ApplicationScoped
public class OnSessionOpenEventListener {

    @Inject
    GameSessions gameSessions;

    void process(@Observes OnSessionOpenEvent event) {
        gameSessions.register(event.session(), event.playerName());
    }

}
