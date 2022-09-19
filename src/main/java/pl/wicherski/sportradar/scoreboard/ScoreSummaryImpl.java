package pl.wicherski.sportradar.scoreboard;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Objects.isNull;

class ScoreSummaryImpl implements ScoreSummary {

    private final List<Game> games;
    private String printableSummary;

    ScoreSummaryImpl(List<Game> games) {
        this.games = games;
    }

    @Override
    public String toPrintableSummary() {
        if (isNull(printableSummary)) {
            printableSummary = prepareSummary();
        }
        return printableSummary;
    }

    private String prepareSummary() {
        return IntStream.range(0, games.size())
                        .mapToObj(i -> (i + 1) + ". " + formatGame(games.get(i)))
                        .collect(Collectors.joining("\n"));
    }

    private String formatGame(Game game) {
        return "%s %s - %s %s".formatted(game.homeTeamName(),
                                         game.score()
                                             .home(),
                                         game.awayTeamName(),
                                         game.score()
                                             .away());
    }

}
