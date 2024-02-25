package org.jakartaee.sample.websocket.listener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jakartaee.sample.websocket.GameSessions;
import org.jakartaee.sample.websocket.event.OnSessionErrorEvent;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class OnSessionErrorEventListener {

    private static final Logger LOG = Logger.getLogger(OnSessionErrorEventListener.class.getName());

    @Inject
    GameSessions gameSessions;

    void process(@Observes OnSessionErrorEvent event) {
        LOG.log(Level.SEVERE, "onError", event.throwable());
        gameSessions.unregister(event.session());
    }

}
