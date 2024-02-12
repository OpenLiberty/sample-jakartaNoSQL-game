package org.jakartaee.sample.game;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jakartaee.sample.game.Movement.PAPER;
import static org.jakartaee.sample.game.Movement.ROCK;

class GamePlayersTest {

    Player player1 = GameTest.randomPlayer1();
    Player player2 = GameTest.randomPlayer1();
    Player somePlayer = GameTest.randomPlayer();

    @Test
    void isPart() {

        GameOver gameOver = new GameOver(UUID.randomUUID().toString(), player1, player2, PAPER, ROCK);

        GamePlayers gamePlayers = GamePlayers.of(gameOver);

        assertSoftly(softly -> {

            softly.assertThat(gamePlayers)
                    .as("GamePlayers is null")
                    .isNotNull();

            softly.assertThat(gamePlayers.players())
                    .as("GamePlayers players is empty")
                    .isNotEmpty()
                    .as("GamePlayers players should contains the player1 and player2")
                    .contains(player1, player2);
        });

    }

    @Test
    void getOpponent() {

        GamePlayers gamePlayers = GamePlayers.of(player1, player2);

        assertSoftly(softly -> {

            softly.assertThat(gamePlayers.getOpponent(player1))
                    .as("the opponent for player1 should be player2")
                    .hasValue(player2);

            softly.assertThat(gamePlayers.getOpponent(player2))
                    .as("the opponent for player2 should be player1")
                    .hasValue(player1);

            softly.assertThat(gamePlayers.getOpponent(somePlayer))
                    .as("the opponent for some player out of players list should be not present")
                    .isNotPresent();

            softly.assertThat(gamePlayers.getOpponent(null))
                    .as("the opponent for a null reference should be not present")
                    .isNotPresent();

        });
    }

    @Test
    void shouldOf() {

        assertSoftly(softly -> {

            softly.assertThat(GamePlayers.of(player1))
                    .as("GamePlayers should be able to be created based on one player")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(player1, player2))
                    .as("GamePlayers should be able to be created based on a players array")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(List.of(player1, player2)))
                    .as("GamePlayers should be able to be created based on a players collection")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(Stream.of(player1, player2)))
                    .as("GamePlayers should be able to be created based on a players stream")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(new WaitingPlayers(UUID.randomUUID().toString(), player1)))
                    .as("GamePlayers should be able to be created based on a " + WaitingPlayers.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(new GameReady(UUID.randomUUID().toString(), player1, player2)))
                    .as("GamePlayers should be able to be created based on a " + GameReady.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(new GameRunning(UUID.randomUUID().toString(), player1, player2, null, null)))
                    .as("GamePlayers should be able to be created based on a " + GameRunning.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(new GameOver(UUID.randomUUID().toString(), player1, player2, ROCK, ROCK)))
                    .as("GamePlayers should be able to be created based on a " + GameOver.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(new GameAbandoned(UUID.randomUUID().toString(), Set.of(player1, player2))))
                    .as("GamePlayers should be able to be created based on a " + GameAbandoned.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(new GameInvalid(UUID.randomUUID().toString())))
                    .as("GamePlayers should be able to be created based on a " + GameInvalid.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(GamePlayers.of(() -> null))
                    .as("GamePlayers should be able to be created based on any " + GameState.class.getSimpleName() + " object")
                    .isNotNull();

        });
    }

    @Test
    void shouldPlayers() {
        GameOver gameState = new GameOver(UUID.randomUUID().toString(), player1, player2, PAPER, ROCK);

        GamePlayers gamePlayers = GamePlayers.of(gameState);

        assertSoftly(softly -> {

            softly.assertThat(gamePlayers)
                    .as("GamePlayers is null")
                    .isNotNull();

            softly.assertThat(gamePlayers.players())
                    .as("GamePlayers players is empty")
                    .isNotEmpty()
                    .as("GamePlayers players should contains the player1 and player2")
                    .contains(player1, player2);


            GamePlayers fromOnePlayer = GamePlayers.of(player1);

            softly.assertThat(fromOnePlayer)
                    .as("GamePlayers should be able to be created based on one player")
                    .isNotNull();

            softly.assertThat(fromOnePlayer.players())
                    .as("GamePlayers players from based on one player should be not empty")
                    .isNotEmpty()
                    .as("GamePlayers players based on one player should contains the player1")
                    .contains(player1);


            GamePlayers gamePlayersFromArray = GamePlayers.of(player1, player2);

            softly.assertThat(gamePlayersFromArray)
                    .as("GamePlayers should be able to be created based on a players array")
                    .isNotNull();

            softly.assertThat(gamePlayersFromArray.players())
                    .as("GamePlayers players based on a players array should be not empty")
                    .isNotEmpty()
                    .as("GamePlayers players based on a players array should contains the player1 and player2")
                    .contains(player1, player2);

            GamePlayers fromPlayerCollection = GamePlayers.of(List.of(player1, player2));

            softly.assertThat(fromPlayerCollection)
                    .as("GamePlayers should be able to be created based on a players collection")
                    .isNotNull();

            softly.assertThat(fromPlayerCollection.players())
                    .as("GamePlayers players based on player collection should be not empty")
                    .isNotEmpty()
                    .as("GamePlayers players based on player collection should contains the player1 and player2")
                    .contains(player1, player2);

            GamePlayers fromPlayerStream = GamePlayers.of(Stream.of(player1, player2));

            softly.assertThat(fromPlayerStream)
                    .as("GamePlayers should be able to be created based on a players stream")
                    .isNotNull();

            softly.assertThat(fromPlayerStream.players())
                    .as("GamePlayers players based on player stream should be not empty")
                    .isNotEmpty()
                    .as("GamePlayers players based on player stream should contains the player1 and player2")
                    .contains(player1, player2);

            GamePlayers fromWaitingPlayers = GamePlayers.of(new WaitingPlayers(UUID.randomUUID().toString(), player1));

            softly.assertThat(fromWaitingPlayers)
                    .as("GamePlayers should be able to be created based on a " + WaitingPlayers.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(fromWaitingPlayers.players())
                    .as("GamePlayers players based on a " + WaitingPlayers.class.getSimpleName() + " object should be not empty")
                    .isNotEmpty()
                    .as("GamePlayers players based on a " + WaitingPlayers.class.getSimpleName() + " object should contains the player1")
                    .contains(player1);

            GamePlayers fromGameReady = GamePlayers.of(new GameReady(UUID.randomUUID().toString(), player1, player2));

            softly.assertThat(fromGameReady)
                    .as("GamePlayers should be able to be created based on a " + GameReady.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(fromGameReady.players())
                    .as("GamePlayers players based on a " + GameReady.class.getSimpleName() + " object should be not empty")
                    .isNotEmpty()
                    .as("GamePlayers players based on a " + GameReady.class.getSimpleName() + " object should contains the player1 and player2")
                    .contains(player1, player2);

            GamePlayers fromGameRunning = GamePlayers.of(new GameRunning(UUID.randomUUID().toString(), player1, player2, null, null));

            softly.assertThat(fromGameRunning)
                    .as("GamePlayers should be able to be created based on a " + GameRunning.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(fromGameRunning.players())
                    .as("GamePlayers players based on a " + GameRunning.class.getSimpleName() + " object should be not empty")
                    .isNotEmpty()
                    .as("GamePlayers players based on a " + GameRunning.class.getSimpleName() + " object should contains the player1 and player2")
                    .contains(player1, player2);


            GamePlayers fromGameOver = GamePlayers.of(new GameOver(UUID.randomUUID().toString(), player1, player2, ROCK, ROCK));

            softly.assertThat(fromGameOver)
                    .as("GamePlayers should be able to be created based on a " + GameOver.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(fromGameOver.players())
                    .as("GamePlayers players based on a " + GameOver.class.getSimpleName() + " object should be not empty")
                    .isNotEmpty()
                    .as("GamePlayers players based on a " + GameOver.class.getSimpleName() + " object should contains the player1 and player2")
                    .contains(player1, player2);


            GamePlayers fromGameAbandoned = GamePlayers.of(new GameAbandoned(UUID.randomUUID().toString(), Set.of(player1, player2)));

            softly.assertThat(fromGameAbandoned)
                    .as("GamePlayers should be able to be created based on a " + GameAbandoned.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(fromGameAbandoned.players())
                    .as("GamePlayers players based on a " + GameAbandoned.class.getSimpleName() + " object should be not empty")
                    .isNotEmpty()
                    .as("GamePlayers players based on a " + GameAbandoned.class.getSimpleName() + " object should contains the player1 and player2")
                    .contains(player1, player2);

            GamePlayers fromGameInvalid = GamePlayers.of(new GameInvalid(UUID.randomUUID().toString()));

            softly.assertThat(fromGameInvalid)
                    .as("GamePlayers should be able to be created based on a " + GameInvalid.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(fromGameInvalid.players())
                    .as("GamePlayers players based on a " + GameAbandoned.class.getSimpleName() + " object should be empty")
                    .isEmpty();

            GamePlayers fromOtherGameState = GamePlayers.of(() -> null);

            softly.assertThat(fromOtherGameState)
                    .as("GamePlayers should be able to be created based on any " + GameState.class.getSimpleName() + " object")
                    .isNotNull();

            softly.assertThat(fromOtherGameState.players())
                    .as("GamePlayers players based on other " + GameState.class.getSimpleName() + " object should be empty")
                    .isEmpty();

        });
    }
}