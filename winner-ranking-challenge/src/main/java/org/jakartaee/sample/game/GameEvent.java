package org.jakartaee.sample.game;

public record GameEvent(Object source, GameState gameState) {
}
