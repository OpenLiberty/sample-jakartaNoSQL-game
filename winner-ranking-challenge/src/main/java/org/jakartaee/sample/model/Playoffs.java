package org.jakartaee.sample.model;

import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Query;
import jakarta.data.repository.Repository;
import jakarta.data.repository.Save;

import java.util.stream.Stream;

@Repository
public interface Playoffs extends DataRepository<GameMatch,String> {

    @Save
    GameMatch add(GameMatch gameMatch);

    @Query("select * from GameMatch where tied=false")
    Stream<GameMatch> nonTiedGameMatches();

    default Ranking winnerRanking(){
       return Ranking.winnerRanking(this);
    }
}
