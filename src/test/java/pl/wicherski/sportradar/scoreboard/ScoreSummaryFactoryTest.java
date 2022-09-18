package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreSummaryFactoryTest {

    private ScoreSummaryFactory scoreSummaryFactory;

    @BeforeEach
    void setUp() {
        scoreSummaryFactory = new ScoreSummaryFactory();
    }

    @Test
    void shouldCreateScoreSummary_whenCreatingSummary() {
        ScoreSummary summary = scoreSummaryFactory.createSummaryFor(List.of());

        assertThat(summary).isNotNull()
                           .isInstanceOf(ScoreSummaryImpl.class);
    }

}