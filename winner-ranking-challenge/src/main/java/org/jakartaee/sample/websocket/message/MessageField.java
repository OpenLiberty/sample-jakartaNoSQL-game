package org.jakartaee.sample.websocket.message;

import java.util.Optional;

public enum MessageField {

    ERROR,
    GAME_ID,
    OPPONENT_NAME,
    OPPONENT_MOVEMENT,
    MOVEMENT;

    public void set(Message message, String value) {
        message.data().put(this, value);
    }

    public void unset(Message message) {
        message.data().remove(this);
    }

    public Optional<String> get(Message message) {
        return Optional.ofNullable(message.data().get(this));
    }

}
