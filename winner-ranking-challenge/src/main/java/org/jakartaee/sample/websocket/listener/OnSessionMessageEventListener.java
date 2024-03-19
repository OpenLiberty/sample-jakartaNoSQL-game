package org.jakartaee.sample.websocket.listener;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.jakartaee.sample.websocket.GameSessions;
import org.jakartaee.sample.websocket.event.OnSessionMessageEvent;
import org.jakartaee.sample.websocket.message.Message;

import static java.util.Objects.isNull;

@ApplicationScoped
public class OnSessionMessageEventListener {

    @Inject
    GameSessions gameSessions;

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
