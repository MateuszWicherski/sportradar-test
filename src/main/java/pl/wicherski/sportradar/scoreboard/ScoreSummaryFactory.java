package pl.wicherski.sportradar.scoreboard;

import java.util.List;

class ScoreSummaryFactory {

    public ScoreSummary createSummaryFor(List<Game> sortedGames) {
        return new ScoreSummaryImpl(sortedGames);
    }

}
