package org.jakartaee.sample.server.listener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jakartaee.sample.server.GameSessions;
import org.jakartaee.sample.server.event.OnSessionMessageEvent;
import org.jakartaee.sample.server.message.Message;

import static java.util.Objects.isNull;

@ApplicationScoped
public class OnSessionMessageEventListener {

    private GameSessions gameSessions;

    @Inject
    public OnSessionMessageEventListener(GameSessions gameSessions) {
        this.gameSessions = gameSessions;
    }

    void onSessionMessage(@Observes OnSessionMessageEvent event) {
        var message = Message.fromJson(event.rawMessage());
        if (isNull(message.type())) {
            gameSessions.receivedInvalidMessage(event.rawMessage(), event.session());
            return;
        }

        switch (message.type()) {
            case NEW_GAME -> gameSessions.playerWantsNewGame(message, event.session());
            case PLAY_GAME -> gameSessions.playerWantsToPlay(message, event.session());
            default -> gameSessions.receivedUnsupportedMessage(message, event.session());
        }
    }
}
