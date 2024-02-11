package org.jakartaee.sample.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jakartaee.sample.game.Movement.PAPER;
import static org.jakartaee.sample.game.Movement.ROCK;

class GameTest {


    Game game;
    Player player1;
    Player player2;

    @BeforeEach
    void given() {
        game = new Game();
        player1 = randomPlayer1();
        player2 = randomPlayer2();
    }

    @Test
    void shouldErrorNewGameWithInvalidArgs() {
        assertThatThrownBy(() -> game.newGame(null))
                .as("Should error with null args")
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void shouldNewGame() {

        assertSoftly(softly -> {

            var gameStateForPlayer1 = game.newGame(player1);

            softly.assertThat(gameStateForPlayer1)
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + WaitingPlayers.class.getSimpleName())
                    .isInstanceOf(WaitingPlayers.class)
                    .as("the returned game state should be equals to the same returned from the latest newGame(player) call")
                    .isEqualTo(game.newGame(player1));

            var gameStateForPlayer2 = game.newGame(player2);

            softly.assertThat(gameStateForPlayer2)
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameRunning.class.getSimpleName())
                    .isInstanceOf(GameRunning.class)
                    .as("the returned game state should be equals to the same returned from the latest newGame(player) call")
                    .isEqualTo(game.newGame(player2));
        });
    }

    @Test
    void shouldLeavingGame() {
        assertSoftly(softly -> {

            softly.assertThat(game.leaveGame(null))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameInvalid.class.getSimpleName())
                    .isInstanceOf(GameInvalid.class);

            softly.assertThat(game.leaveGame(randomPlayer()))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameInvalid.class.getSimpleName() + " when a given player didn't initiate a game previously")
                    .isInstanceOf(GameInvalid.class);

            game.newGame(player1);

            softly.assertThat(game.leaveGame(player1))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameAbandoned.class.getSimpleName() + " when a given player did initiate a game previously")
                    .isInstanceOf(GameAbandoned.class);


            game.newGame(player1);
            game.newGame(player2);

            softly.assertThat(game.leaveGame(player1))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameAbandoned.class.getSimpleName() + " when a given player is participating on the game")
                    .isInstanceOf(GameAbandoned.class);

            game.newGame(player1);
            game.newGame(player2);
            game.playGame(player2, ROCK);

            softly.assertThat(game.leaveGame(player1))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameAbandoned.class.getSimpleName() + " when a given player is participating on the game")
                    .isInstanceOf(GameAbandoned.class);


        });
    }

    @Test
    void shouldErrorPlayGameWithInvalidArgs() {
        assertSoftly(softly -> {
            softly.assertThatThrownBy(() ->
                            game.playGame(player1, null))
                    .as("should error when just the player is provided as arg")
                    .isInstanceOf(NullPointerException.class);
            softly.assertThatThrownBy(() ->
                            game.playGame(null, ROCK))
                    .as("should error when just the movement is provided as arg")
                    .isInstanceOf(NullPointerException.class);
            softly.assertThatThrownBy(() ->
                            game.playGame(null, null))
                    .as("Should error with null args")
                    .isInstanceOf(NullPointerException.class);
        });
    }

    @Test
    void shouldPlayGame() {
        assertSoftly(softly -> {

            softly.assertThat(game.playGame(player1, ROCK))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameInvalid.class.getSimpleName() + " when a given player didn't initiate a game previously")
                    .isInstanceOf(GameInvalid.class);

            game.newGame(player1);

            softly.assertThat(game.playGame(player1, ROCK))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + WaitingPlayers.class.getSimpleName() + " when a given player is waiting for an opponent")
                    .isInstanceOf(WaitingPlayers.class);

            game.newGame(player2);

            softly.assertThat(game.playGame(player1, ROCK))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameRunning.class.getSimpleName() + " when a given player play his movement but the opponent didn't play his movement yet")
                    .isInstanceOf(GameRunning.class);

            softly.assertThat(game.playGame(player1, ROCK))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameRunning.class.getSimpleName() + " when a given player play his movement but the opponent didn't play his movement yet")
                    .isInstanceOf(GameRunning.class);

            softly.assertThat(game.playGame(player1, PAPER))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameRunning.class.getSimpleName() + " when a given player play his movement but the opponent didn't play his movement yet")
                    .isInstanceOf(GameRunning.class);

            softly.assertThat(game.playGame(player2, ROCK))
                    .as("the returned game state should be non-null")
                    .isNotNull()
                    .as("the returned game state should be " + GameOver.class.getSimpleName() + " when a given player play his movement then completing the game")
                    .isInstanceOf(GameOver.class);

        });
    }

    public static Player randomPlayer1() {
        return Player.of(UUID.randomUUID().toString(), "player1");
    }

    public static Player randomPlayer2() {
        return Player.of(UUID.randomUUID().toString(), "player2");
    }

    public static Player randomPlayer() {
        return Player.of(UUID.randomUUID().toString(), "player-" + System.currentTimeMillis());
    }

}