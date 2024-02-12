package org.jakartaee.sample.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jakartaee.sample.game.Movement.PAPER;
import static org.jakartaee.sample.game.Movement.ROCK;

class GameEventListenerTest {

    Game game;
    Player player1;
    Player player2;

    @BeforeEach
    void given() {
        game = new Game();
        player1 = GameTest.randomPlayer1();
        player2 = GameTest.randomPlayer2();
    }

    @Test
    void shouldCaptureEventsByOneOnlyListener() {


        assertSoftly(softly -> {

            List<GameState> events = new ArrayList<>();
            Consumer<GameState> listener = events::add;

            softly.assertThat(game.addListener(null))
                    .as("should return the same game instance on adding a listener")
                    .isSameAs(game);

            softly.assertThat(game.addListener(listener))
                    .as("should return the same game instance on adding a listener")
                    .isSameAs(game);

            softly.assertThat(events)
                    .as("events should be empty")
                    .isEmpty();

            var gameId = game.newGame(player1).gameId();
            game.newGame(player2);
            game.playGame(player1, ROCK);
            game.playGame(player2, PAPER);

            softly.assertThat(events)
                    .as("events should be not empty")
                    .isNotEmpty()
                    .containsExactly(
                            new WaitingPlayers(gameId, player1),
                            new GameReady(gameId, player1, player2),
                            new GameRunning(gameId, player1, player2, ROCK, null),
                            new GameOver(gameId, player1, player2, ROCK, PAPER)
                    );

            events.clear();

            softly.assertThat(game.removeListener(null))
                    .as("should return false when removing a null listener")
                    .isFalse();


            softly.assertThat(game.removeListener(listener))
                    .as("should return true when removing an added listener")
                    .isTrue();

            softly.assertThat(game.removeListener(listener))
                    .as("should return false when removing an non-added listener previously")
                    .isFalse();

            gameId = game.newGame(player1).gameId();
            game.newGame(player2);
            game.playGame(player1, ROCK);
            game.playGame(player2, PAPER);

            softly.assertThat(events)
                    .as("should be not collected any event")
                    .isEmpty();

            events.clear();

            softly.assertThat(game.addListener(listener))
                    .as("should return the same game instance on adding a listener")
                    .isSameAs(game);

            gameId = game.newGame(player2).gameId();
            game.newGame(player2);
            game.newGame(player1);
            game.playGame(player1, PAPER);
            game.playGame(player2, ROCK);

            softly.assertThat(events)
                    .as("events should be not empty")
                    .isNotEmpty()
                    .containsExactly(
                            new WaitingPlayers(gameId, player2),
                            new GameReady(gameId, player2, player1),
                            new GameRunning(gameId, player2, player1, null, PAPER),
                            new GameOver(gameId, player2, player1, ROCK, PAPER)
                    );

            events.clear();
            gameId = game.newGame(player1).gameId();
            game.newGame(player2);
            game.leaveGame(player1);

            softly.assertThat(events)
                    .as("events should be not empty")
                    .isNotEmpty()
                    .containsExactly(
                            new WaitingPlayers(gameId, player1),
                            new GameReady(gameId, player1, player2),
                            new GameAbandoned(gameId, Set.of(player1, player2))
                    );

            events.clear();
            gameId = game.newGame(player1).gameId();
            game.newGame(player2);
            game.playGame(player2, ROCK);
            game.leaveGame(player1);

            softly.assertThat(events)
                    .as("events should be not empty")
                    .isNotEmpty()
                    .containsExactly(
                            new WaitingPlayers(gameId, player1),
                            new GameReady(gameId, player1, player2),
                            new GameRunning(gameId, player1, player2, null, ROCK),
                            new GameAbandoned(gameId, Set.of(player1, player2))
                    );
        });


    }


    @Test
    void shouldCaptureEventsByMultipleListeners() {
        assertSoftly(softly -> {

            List<GameState> eventsCapturedByListener1 = new ArrayList<>();
            List<GameState> eventsCapturedByListener2 = new ArrayList<>();

            Consumer<GameState> listener1 = eventsCapturedByListener1::add;
            Consumer<GameState> listener2 = eventsCapturedByListener2::add;

            game.addListener(listener1).addListener(listener2);

            var gameId = game.newGame(player1).gameId();
            game.newGame(player2);
            game.playGame(player1, ROCK);
            game.playGame(player2, PAPER);

            softly.assertThat(eventsCapturedByListener1)
                    .as("events captured by listener1 should be not empty")
                    .isNotEmpty()
                    .as("events captured by listener1 should be follow an specific order")
                    .containsExactly(
                            new WaitingPlayers(gameId, player1),
                            new GameReady(gameId, player1, player2),
                            new GameRunning(gameId, player1, player2, ROCK, null),
                            new GameOver(gameId, player1, player2, ROCK, PAPER)
                    );

            softly.assertThat(eventsCapturedByListener2)
                    .as("events captured by listener2 should be not empty")
                    .isNotEmpty()
                    .as("events captured by listener2 should be follow an specific order")
                    .containsExactly(
                            new WaitingPlayers(gameId, player1),
                            new GameReady(gameId, player1, player2),
                            new GameRunning(gameId, player1, player2, ROCK, null),
                            new GameOver(gameId, player1, player2, ROCK, PAPER)
                    );

        });
    }

    @Test
    void shouldCaptureEventsByOneOfTheListeners() {
        assertSoftly(softly -> {

            List<GameState> eventsCapturedByListener1 = new ArrayList<>();

            Consumer<GameState> listener1 = eventsCapturedByListener1::add;
            Consumer<GameState> listener2 = gameState -> {
                throw new UnsupportedOperationException("cannot accept events!");
            };

            game.addListener(listener1).addListener(listener2);

            var gameId = game.newGame(player1).gameId();
            game.newGame(player2);
            game.playGame(player1, ROCK);
            game.playGame(player2, PAPER);

            softly.assertThat(eventsCapturedByListener1)
                    .as("events captured by listener1 should be not empty")
                    .isNotEmpty()
                    .as("events captured by listener1 should be follow an specific order")
                    .containsExactly(
                            new WaitingPlayers(gameId, player1),
                            new GameReady(gameId, player1, player2),
                            new GameRunning(gameId, player1, player2, ROCK, null),
                            new GameOver(gameId, player1, player2, ROCK, PAPER)
                    );
        });
    }
}
