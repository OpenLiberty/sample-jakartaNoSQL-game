package org.jakartaee.sample.model;

import jakarta.nosql.Column;
import jakarta.nosql.Entity;
import jakarta.nosql.Id;

@Entity
public record GameMatch (
        @Id
        String id,

        @Column
        PlayerInfo playerA,

        @Column
        PlayerInfo playerB,

        @Column
        PlayerInfo winner,

        @Column
        PlayerInfo loser,

        @Column
        Boolean tied
){
}
