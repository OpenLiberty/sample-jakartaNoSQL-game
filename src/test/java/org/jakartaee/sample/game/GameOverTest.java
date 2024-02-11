package org.jakartaee.sample.game;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.jakartaee.sample.game.Movement.PAPER;
import static org.jakartaee.sample.game.Movement.ROCK;

class GameOverTest {

    Player player1 = GameTest.randomPlayer1();
    Player player2 = GameTest.randomPlayer1();

    @Test
    void shouldGameOverTied() {
        final GameOver gameOver = new GameOver(UUID.randomUUID().toString(), player1, player2, ROCK, ROCK);
        assertSoftly(softly -> {

            softly.assertThat(gameOver.isTied())
                    .as("game should be tied")
                    .isTrue();

            softly.assertThat(gameOver.players())
                    .as("players should be player1 and player2")
                    .containsExactly(player1, player2);

            softly.assertThat(gameOver.playerA())
                    .as("playerA should be player1")
                    .isEqualTo(player1);

            softly.assertThat(gameOver.playerAMovement())
                    .as("playerA movement should be ROCK")
                    .isEqualTo(ROCK);

            softly.assertThat(gameOver.playerAInfo())
                    .as("playerA info should have player1 as player and ROCK as movement")
                    .isEqualTo(new GameOver.PlayerInfo(gameOver.gameId(), player1, ROCK));

            softly.assertThat(gameOver.playerB())
                    .as("playerB should be player2")
                    .isEqualTo(player2);

            softly.assertThat(gameOver.playerBMovement())
                    .as("playerB movement should be ROCK")
                    .isEqualTo(ROCK);

            softly.assertThat(gameOver.playerBInfo())
                    .as("playerA info should have player2 as player and ROCK as movement")
                    .isEqualTo(new GameOver.PlayerInfo(gameOver.gameId(), player2, ROCK));


            softly.assertThat(gameOver.winner())
                    .as("a tied game's winner should be not present")
                    .isNotPresent();

            softly.assertThat(gameOver.winnerMovement())
                    .as("a tied game's winner movement should be not present")
                    .isNotPresent();

            softly.assertThat(gameOver.winnerInfo())
                    .as("a tied game's winner info should be not present")
                    .isNotPresent();

            softly.assertThat(gameOver.loser())
                    .as("a tied game's loser should be not present")
                    .isNotPresent();

            softly.assertThat(gameOver.loserMovement())
                    .as("a tied game's loser movement should be not present")
                    .isNotPresent();

            softly.assertThat(gameOver.loserInfo())
                    .as("a tied game's loser info should be not present")
                    .isNotPresent();

        });
    }

    @Test
    void shouldGameOver() {
        final GameOver gameOver = new GameOver(UUID.randomUUID().toString(), player1, player2, PAPER, ROCK);
        assertSoftly(softly -> {

            softly.assertThat(gameOver.isTied())
                    .as("game should not be tied")
                    .isFalse();

            softly.assertThat(gameOver.players())
                    .as("players should be player1 and player2")
                    .containsExactly(player1, player2);

            softly.assertThat(gameOver.playerA())
                    .as("playerA should be player1")
                    .isEqualTo(player1);

            softly.assertThat(gameOver.playerAMovement())
                    .as("playerA movement should be PAPER")
                    .isEqualTo(PAPER);

            softly.assertThat(gameOver.playerAInfo())
                    .as("playerA info should have player1 as player and PAPER as movement")
                    .isEqualTo(new GameOver.PlayerInfo(gameOver.gameId(), player1, PAPER));

            softly.assertThat(gameOver.playerB())
                    .as("playerB should be player2")
                    .isEqualTo(player2);

            softly.assertThat(gameOver.playerBMovement())
                    .as("playerB movement should be ROCK")
                    .isEqualTo(ROCK);

            softly.assertThat(gameOver.playerBInfo())
                    .as("playerA info should have player2 as player and ROCK as movement")
                    .isEqualTo(new GameOver.PlayerInfo(gameOver.gameId(), player2, ROCK));


            softly.assertThat(gameOver.winner())
                    .as("a tied game's winner should be present")
                    .isPresent()
                    .as("the game's winner should be player1")
                    .hasValue(player1);

            softly.assertThat(gameOver.winnerMovement())
                    .as("a tied game's winner movement should be not present")
                    .isPresent()
                    .as("the game's winner movement should be PAPER")
                    .hasValue(PAPER);

            softly.assertThat(gameOver.winnerInfo())
                    .as("a tied game's winner info should be not present")
                    .isPresent()
                    .as("the game's winner info should be player1 and PAPER")
                    .hasValue(new GameOver.WinnerInfo(gameOver.gameId(), player1, PAPER));

            softly.assertThat(gameOver.loser())
                    .as("a tied game's loser should be present")
                    .isPresent()
                    .as("the game's loser should be player2")
                    .hasValue(player2);

            softly.assertThat(gameOver.loserMovement())
                    .as("the game's loser movement should be present")
                    .isPresent()
                    .as("the game's loser movement should be ROCK")
                    .hasValue(ROCK);

            softly.assertThat(gameOver.loserInfo())
                    .as("a tied game's loser info should be present")
                    .isPresent()
                    .as("the game's loser info should be player2 and ROCK")
                    .hasValue(new GameOver.LoserInfo(gameOver.gameId(), player2, ROCK));

        });
    }

}