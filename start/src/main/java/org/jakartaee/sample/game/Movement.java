package org.jakartaee.sample.game;

public enum Movement {

    PAPER,
    ROCK,
    SCISSORS;

    public boolean beats(Movement other) {
        return switch (this){
            case ROCK -> SCISSORS.equals(other);
            case SCISSORS -> PAPER.equals(other);
            case PAPER -> ROCK.equals(other);
        };
    }

}
