package org.jakartaee.sample.model;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.jakartaee.sample.game.GameOver;

@ApplicationScoped
public class GameMatchCapturer {

    @Inject
    @Database(DatabaseType.DOCUMENT)
    Playoffs playoffs;

    public void captureAndPersist(@Observes GameOver gameOver){

            var gameMatch  = new GameMatch(
                    gameOver.gameId(),
                    PlayerInfo.of(gameOver.playerAInfo()),
                    PlayerInfo.of(gameOver.playerBInfo()),
                    gameOver.winnerInfo().map(PlayerInfo::of).orElse(PlayerInfo.NOBODY),
                    gameOver.winnerInfo().map(PlayerInfo::of).orElse(PlayerInfo.NOBODY),
                    gameOver.isTied()
            );

            playoffs.add(gameMatch);
    }

}
