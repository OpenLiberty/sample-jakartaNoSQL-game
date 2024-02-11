package org.jakartaee.sample.server.listener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jakartaee.sample.server.GameSessions;
import org.jakartaee.sample.server.event.OnSessionCloseEvent;

@ApplicationScoped
public class OnSessionCloseEventListener {

    private GameSessions gameSessions;

    @Inject
    public OnSessionCloseEventListener(GameSessions gameSessions) {
        this.gameSessions = gameSessions;
    }

    void process(@Observes OnSessionCloseEvent event) {
        gameSessions.unregister(event.session());
    }

}
