package pl.wicherski.sportradar.scoreboard;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.AdditionalAnswers.answersWithDelay;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ThreadSafeLiveScoreBoardTest {

    private static final String TEAM_2 = "team2";
    private static final String TEAM_1 = "team1";
    private static final String TEAM_3 = "team3";
    private static final String TEAM_4 = "team4";

    @InjectMocks
    private ThreadSafeLiveScoreBoard board;
    @Mock
    private LiveScoreBoard delegateMock;

    @Test
    void shouldLockStartGameExecution_whenCalledInParallel() {
        GameId gameId = GameId.generate();
        long sleepyTime = 100L;
        when(delegateMock.startGame(TEAM_1, TEAM_2)).then(answersWithDelay(sleepyTime,
                                                                           answer -> gameId));
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> board.startGame(TEAM_1, TEAM_2));
        // block until the thread starts execution
        await().atMost(sleepyTime, TimeUnit.MILLISECONDS)
               .pollInterval(5, TimeUnit.MILLISECONDS)
               .untilAsserted(() -> verify(delegateMock).startGame(TEAM_1, TEAM_2));

        executorService.submit(() -> board.startGame(TEAM_3, TEAM_4));

        // second thread should wait for the first one to complete (it will take little less than sleep time at this point)
        await().atLeast(sleepyTime / 2, TimeUnit.MILLISECONDS)
               .pollInterval(5, TimeUnit.MILLISECONDS)
               .untilAsserted(() -> verify(delegateMock).startGame(TEAM_3, TEAM_4));
    }

    @Test
    void shouldLockUpdateScoreExecution_whenCalledInParallel() {
        GameId gameId1 = GameId.generate();
        GameId gameId2 = GameId.generate();
        Score score = Score.of(1, 3);
        long sleepyTime = 100L;
        doAnswer(answersWithDelay(sleepyTime, answer -> null)).when(delegateMock)
                                                              .updateScore(gameId1, score);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> board.updateScore(gameId1, score));
        // block until the thread starts execution
        await().atMost(sleepyTime, TimeUnit.MILLISECONDS)
               .pollInterval(5, TimeUnit.MILLISECONDS)
               .untilAsserted(() -> verify(delegateMock).updateScore(gameId1, score));

        executorService.submit(() -> board.updateScore(gameId2, score));

        // second thread should wait for the first one to complete (it will take little less than sleep time at this point)
        await().atLeast(sleepyTime / 2, TimeUnit.MILLISECONDS)
               .pollInterval(5, TimeUnit.MILLISECONDS)
               .untilAsserted(() -> verify(delegateMock).updateScore(gameId2, score));
    }

    @Test
    void shouldLockFinishGameExecution_whenCalledInParallel() {
        GameId gameId1 = GameId.generate();
        GameId gameId2 = GameId.generate();
        long sleepyTime = 100L;
        doAnswer(answersWithDelay(sleepyTime, answer -> null)).when(delegateMock)
                                                              .finishGame(gameId1);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> board.finishGame(gameId1));
        // block until the thread starts execution
        await().atMost(sleepyTime, TimeUnit.MILLISECONDS)
               .pollInterval(5, TimeUnit.MILLISECONDS)
               .untilAsserted(() -> verify(delegateMock).finishGame(gameId1));

        executorService.submit(() -> board.finishGame(gameId2));

        // second thread should wait for the first one to complete (it will take little less than sleep time at this point)
        await().atLeast(sleepyTime / 2, TimeUnit.MILLISECONDS)
               .pollInterval(5, TimeUnit.MILLISECONDS)
               .untilAsserted(() -> verify(delegateMock).finishGame(gameId2));
    }

    @Test
    void shouldNotLockGenerateSummaryExecution_whenCalledInParallel() {
        long sleepyTime = 100L;
        when(delegateMock.getSummary()).then(answersWithDelay(sleepyTime, answer -> null));
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> board.getSummary());
        // block until the thread starts execution
        await().atMost(sleepyTime, TimeUnit.MILLISECONDS)
               .pollInterval(5, TimeUnit.MILLISECONDS)
               .untilAsserted(() -> verify(delegateMock).getSummary());

        executorService.submit(() -> board.getSummary());

        // second thread should not wait for the first one to complete (there will be two calls for summary at this point)
        await().atMost(sleepyTime / 2, TimeUnit.MILLISECONDS)
               .pollInterval(5, TimeUnit.MILLISECONDS)
               .untilAsserted(() -> verify(delegateMock, times(2)).getSummary());
    }

}