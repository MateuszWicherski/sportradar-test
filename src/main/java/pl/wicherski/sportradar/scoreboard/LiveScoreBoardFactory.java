package pl.wicherski.sportradar.scoreboard;

import java.time.Instant;
import java.util.HashMap;

import static pl.wicherski.sportradar.scoreboard.GameComparators.HIGHEST_OVERALL_SCORE;
import static pl.wicherski.sportradar.scoreboard.GameComparators.RECENT_FIRST;

/**
 * Factory class for obtaining instances of {@link LiveScoreBoard}. It is implemented with singleton pattern.
 * To use obtain its object, please use {@link #getInstance()} method.
 */
public final class LiveScoreBoardFactory {

    private static final LiveScoreBoardFactory INSTANCE = new LiveScoreBoardFactory();

    private final TimeProvider timeProvider = Instant::now;
    private final ScoreSummaryFactory scoreSummaryFactory = new ScoreSummaryFactory();

    private LiveScoreBoardFactory() {
    }

    /**
     * Factory method for creating new {@link LiveScoreBoard}. Created instance stores games in memory and produces {@link ScoreSummary}
     * with games ordered by overall score and then by start time (recent first), in format:
     * <pre>
     * 1. Uruguay 6 - Italy 6
     * 2. Spain 10 - Brazil 2
     * 3. Mexico 0 - Canada 5
     * 4. Argentina 3 - Australia 1
     * 5. Germany 2 - France 2
     * </pre>
     * <p>
     * Created board IS NOT thread safe.
     *
     * @return configured new {@link LiveScoreBoard}
     */
    public LiveScoreBoard newBoard() {
        return new InMemoryLiveScoreBoard(new HashMap<>(),
                                          timeProvider,
                                          scoreSummaryFactory,
                                          HIGHEST_OVERALL_SCORE.thenComparing(RECENT_FIRST));
    }

    /**
     * Factory method for creating thread-safe instances of {@link LiveScoreBoard}. Thread safety is achieved by locking modifying operations on the board.
     * It uses {@link #newBoard()} instance for behavior implementation.
     * <p>
     * Due to locking, for single-threaded usage {@link #newBoard()} will have better performance.
     *
     * @return configured, thread-sage {@link LiveScoreBoard}
     */
    public LiveScoreBoard newThreadSafeBoard() {
        return new ThreadSafeLiveScoreBoard(newBoard());
    }

    /**
     * Returns instance of {@link LiveScoreBoardFactory} (the same each time).
     *
     * @return singleton instance of the class
     */
    public static LiveScoreBoardFactory getInstance() {
        return INSTANCE;
    }

}
