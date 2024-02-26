package org.jakartaee.sample.game;

public record Player(String id, String name) {
    public static Player of(String id, String username) {
        return new Player(id, username);
    }
}





