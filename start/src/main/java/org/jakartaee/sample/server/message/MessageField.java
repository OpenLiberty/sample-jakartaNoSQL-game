package org.jakartaee.sample.server.message;

import java.util.Optional;

public enum MessageField {

    error,
    gameId,
    opponentName,
    opponentMovement,
    movement;

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
