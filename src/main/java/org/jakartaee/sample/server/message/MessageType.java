package org.jakartaee.sample.server.message;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Consumer;

public enum MessageType {
    CONNECT,
    CONNECTED,
    NEW_GAME,
    WAITING_PLAYERS,
    PLAY_GAME,
    ERROR,
    GAME_INVALID,
    GAME_READY,
    GAME_RUNNING,
    GAME_OVER_ABANDONED,
    GAME_OVER_YOU_WIN,
    GAME_OVER_YOU_LOSE,
    GAME_OVER_DRAW;

    @SafeVarargs
    public final Message build(Consumer<MessageSetter>...consumers) {
        var consumer = Arrays.stream(consumers)
                .filter(Objects::nonNull)
                .reduce(m->{},Consumer::andThen);
        Message message = new Message(this, new LinkedHashMap<>());
        consumer.accept(MessageSetter.of(message));
        return message;
    }
}
