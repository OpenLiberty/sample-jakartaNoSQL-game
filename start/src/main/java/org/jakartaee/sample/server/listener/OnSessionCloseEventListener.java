package org.jakartaee.sample.server.listener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jakartaee.sample.server.GameSessions;
import org.jakartaee.sample.server.event.OnSessionCloseEvent;

@ApplicationScoped
public class OnSessionCloseEventListener {

    @Inject
    GameSessions gameSessions;

    void process(@Observes OnSessionCloseEvent event) {
        gameSessions.unregister(event.session());
    }

}
