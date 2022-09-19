package pl.wicherski.sportradar.scoreboard;

import java.util.Comparator;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingInt;

final class GameComparators {

    static final Comparator<Game> HIGHEST_OVERALL_SCORE = comparingInt(GameComparators::sumGameScore).reversed();

    static final Comparator<Game> RECENT_FIRST = comparing((Game::creationTimestamp)).reversed();

    private GameComparators() {
    }

    private static int sumGameScore(Game game) {
        return game.score()
                   .away() + game.score()
                                 .home();
    }

}
