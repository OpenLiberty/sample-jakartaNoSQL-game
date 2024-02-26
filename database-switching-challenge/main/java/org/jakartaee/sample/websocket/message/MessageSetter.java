package org.jakartaee.sample.websocket.message;

public record MessageSetter(Message message) {

    public static MessageSetter of(Message message) {
        return new MessageSetter(message);
    }

    public MessageSetter set(MessageField field, String value) {
        field.set(this.message, value);
        return MessageSetter.of(message);
    }

    public MessageSetter unset(MessageField field) {
        field.unset(this.message);
        return MessageSetter.of(message);
    }

}
