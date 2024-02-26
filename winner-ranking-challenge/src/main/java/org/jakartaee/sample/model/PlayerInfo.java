package org.jakartaee.sample.model;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import org.jakartaee.sample.game.GameOver;

@Entity
public record PlayerInfo(

        @Column
        String name,

        @Column
        String movement
) {

    public static final PlayerInfo NOBODY = new PlayerInfo("", "");

    public static PlayerInfo of(GameOver.PlayerInfo playerInfo) {
        if (playerInfo == null)
            return NOBODY;
        return new PlayerInfo(playerInfo.player().name(), playerInfo.movement().toString());
    }

}
