package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScoreTest {

    @Test
    void shouldCreateScore_whenCreatingScore_zeroToZero() {
        Score score = Score.of(0, 0);

        assertThat(score).extracting(Score::home, Score::away)
                         .containsExactly(0, 0);
    }

    @Test
    void shouldCreateScore_whenCreatingScore_withPositiveValues() {
        Score score = Score.of(1, 1);

        assertThat(score).extracting(Score::home, Score::away)
                         .containsExactly(1, 1);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenCreatingScore_withNegativeHomeScore() {
        assertThatThrownBy(() -> Score.of(-1, 5)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowIllegalArgumentException_whenCreatingScore_withNegativeAwayScore() {
        assertThatThrownBy(() -> Score.of(5, -1)).isInstanceOf(IllegalArgumentException.class);
    }

}