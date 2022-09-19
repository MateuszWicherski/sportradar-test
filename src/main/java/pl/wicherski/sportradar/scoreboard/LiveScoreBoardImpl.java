package pl.wicherski.sportradar.scoreboard;

import java.util.Comparator;
import java.util.List;

import static java.util.Objects.isNull;

class LiveScoreBoardImpl implements LiveScoreBoard {

    private final GameRepository gameRepository;
    private final TimeProvider timeProvider;
    private final ScoreSummaryFactory scoreSummaryFactory;
    private final Comparator<Game> gamesSortingComparator;

    LiveScoreBoardImpl(GameRepository gameRepository,
                       TimeProvider timeProvider,
                       ScoreSummaryFactory scoreSummaryFactory,
                       Comparator<Game> gamesSortingComparator) {
        this.gameRepository = gameRepository;
        this.timeProvider = timeProvider;
        this.scoreSummaryFactory = scoreSummaryFactory;
        this.gamesSortingComparator = gamesSortingComparator;
    }

    @Override
    public GameId startGame(String homeTeam, String awayTeam) {
        if (isNull(homeTeam) || isNull(awayTeam)) {
            throw new IllegalArgumentException("Team name cannot be null! Home=%s, Away=%s".formatted(homeTeam,
                                                                                                      awayTeam));
        }

        Game game = new Game(homeTeam, awayTeam, Score.of(0, 0), timeProvider.now());
        GameId gameId = GameId.generate();
        gameRepository.save(gameId, game);
        return gameId;
    }

    @Override
    public void finishGame(GameId gameId) {
        if (isNull(gameId)) {
            throw new IllegalArgumentException("Game ID cannot be null!");
        }
        gameRepository.delete(gameId);
    }

    @Override
    public void updateScore(GameId gameId, Score score) throws GameNotFoundException {
        if (isNull(gameId) || isNull(score)) {
            throw new IllegalArgumentException("Game ID and score cannot be null! GameID=%s, Score=%s".formatted(
                    gameId,
                    score));
        }

        Game gameToUpdate = gameRepository.get(gameId)
                                          .orElseThrow(() -> new GameNotFoundException(gameId));
        Game updatedGame = gameToUpdate.withUpdatedScore(score);
        gameRepository.update(gameId, updatedGame);
    }

    @Override
    public ScoreSummary getSummary() {
        List<Game> sortedGames = gameRepository.getAll()
                                               .stream()
                                               .sorted(gamesSortingComparator)
                                               .toList();
        return scoreSummaryFactory.createSummaryFor(sortedGames);
    }

}
