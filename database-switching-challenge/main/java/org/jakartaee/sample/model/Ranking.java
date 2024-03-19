package org.jakartaee.sample.model;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record Ranking(Map<String, Integer> data) {
    public static Ranking winnerRanking(Playoffs playoffs) {

        var data =
                // getting the game matches that are not tied
                playoffs.nonTiedGameMatches()
                // grouping by winner's name and summarize by game match
                .collect(Collectors.groupingBy(
                        g -> g.winner().name(),
                        Collectors.collectingAndThen(Collectors.toList(), Collection::size)))
                .entrySet()
                .stream()
                // sorting the results by number of game match in descending order
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                // collecting as a LinkedHashMap to keep the sorted items
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        return new Ranking(data);
    }
}
