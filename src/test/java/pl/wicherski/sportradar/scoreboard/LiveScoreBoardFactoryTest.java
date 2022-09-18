package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LiveScoreBoardFactoryTest {

    @Test
    void shouldCreateInMemoryBoard_whenCreatingNewBoard() {
        LiveScoreBoardFactory instance = LiveScoreBoardFactory.getInstance();

        LiveScoreBoard liveScoreBoard = instance.newBoard();

        assertThat(liveScoreBoard).isNotNull()
                                  .isInstanceOf(InMemoryLiveScoreBoard.class);
    }

    @Test
    void shouldCreateThreadSafeBoard() {
        LiveScoreBoardFactory instance = LiveScoreBoardFactory.getInstance();

        LiveScoreBoard liveScoreBoard = instance.newThreadSafeBoard();

        assertThat(liveScoreBoard).isNotNull()
                                  .isInstanceOf(ThreadSafeLiveScoreBoard.class);
    }

}