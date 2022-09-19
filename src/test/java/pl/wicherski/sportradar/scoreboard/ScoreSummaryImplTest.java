package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreSummaryImplTest {

    @Test
    void shouldReturnGamesList_whenFormattingSummary() {
        List<Game> games = List.of(
                new Game("Uruguay", "Italy", Score.of(6, 6), Instant.now()),
                new Game("Spain", "Brazil", Score.of(10, 2), Instant.now()),
                new Game("Mexico", "Canada", Score.of(0, 5), Instant.now()),
                new Game("Argentina", "Australia", Score.of(3, 1), Instant.now()),
                new Game("Germany", "France", Score.of(2, 2), Instant.now())
        );
        ScoreSummary scoreSummary = new ScoreSummaryImpl(games);

        String printableSummary = scoreSummary.toPrintableSummary();

        assertThat(printableSummary)
                .isEqualTo("""
                           1. Uruguay 6 - Italy 6
                           2. Spain 10 - Brazil 2
                           3. Mexico 0 - Canada 5
                           4. Argentina 3 - Australia 1
                           5. Germany 2 - France 2""");
    }

    @Test
    void shouldReturnEmptyString_whenFormattingSummary_andThereAreNoGames() {
        List<Game> games = List.of();
        ScoreSummary scoreSummary = new ScoreSummaryImpl(games);

        String printableSummary = scoreSummary.toPrintableSummary();

        assertThat(printableSummary)
                .isEmpty();
    }

    @Test
    void shouldReturnCachedSummary_whenFormattingSummary_multipleTimes() {
        List<Game> games = List.of(new Game("Uruguay", "Italy", Score.of(6, 6), Instant.now()));
        ScoreSummary scoreSummary = new ScoreSummaryImpl(games);

        String printableSummary1 = scoreSummary.toPrintableSummary();
        String printableSummary2 = scoreSummary.toPrintableSummary();

        assertThat(printableSummary1)
                .isSameAs(printableSummary2);
    }

}