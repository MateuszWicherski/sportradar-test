package pl.wicherski.sportradar.scoreboard;

import org.assertj.core.api.AbstractAssert;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public final class GameAssert extends AbstractAssert<GameAssert, Game> {

    private GameAssert(Game game) {
        super(game, GameAssert.class);
    }

    public static GameAssert assertThatGame(Game game) {
        return new GameAssert(game);
    }

    public GameAssert hasHomeTeam(String homeTeam) {
        isNotNull();
        assertThat(actual.homeTeamName()).isEqualTo(homeTeam);
        return this;
    }

    public GameAssert hasAwayTeam(String awayTeam) {
        isNotNull();
        assertThat(actual.awayTeamName()).isEqualTo(awayTeam);
        return this;
    }

    public GameAssert hasScore(Score score) {
        isNotNull();
        assertThat(actual.score()).isEqualTo(score);
        return this;
    }

    public GameAssert wasCreatedAt(Instant creationTime) {
        isNotNull();
        assertThat(actual.creationTimestamp()).isEqualTo(creationTime);
        return this;
    }

}
