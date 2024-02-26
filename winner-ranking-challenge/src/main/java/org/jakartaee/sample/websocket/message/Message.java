package org.jakartaee.sample.websocket.message;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import java.util.Map;

public record Message(MessageType type, Map<MessageField, String> data) {

    private static final Jsonb JSONB = JsonbBuilder.create();

    public String toJson() {
        return JSONB.toJson(this);
    }

    public static Message fromJson(String json) {
        return JSONB.fromJson(json, Message.class);
    }
}
