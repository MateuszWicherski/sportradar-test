package pl.wicherski.sportradar.scoreboard;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;

class InMemoryLiveScoreBoard implements LiveScoreBoard {

    private final Map<GameId, Game> games;
    private final TimeProvider timeProvider;
    private final ScoreSummaryFactory scoreSummaryFactory;
    private final Comparator<Game> gamesSortingComparator;

    InMemoryLiveScoreBoard(Map<GameId, Game> games,
                           TimeProvider timeProvider,
                           ScoreSummaryFactory scoreSummaryFactory, Comparator<Game> gamesSortingComparator) {
        this.games = games;
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
        games.put(gameId, game);
        return gameId;
    }

    @Override
    public void finishGame(GameId gameId) {
        if (isNull(gameId)) {
            throw new IllegalArgumentException("Game ID cannot be null!");
        }
        games.remove(gameId);
    }

    @Override
    public void updateScore(GameId gameId, Score score) throws GameNotFoundException {
        if (isNull(gameId) || isNull(score)) {
            throw new IllegalArgumentException("Game ID and score cannot be null! GameID=%s, Score=%s".formatted(
                    gameId,
                    score));
        }

        Game gameToUpdate = Optional.ofNullable(games.get(gameId))
                                    .orElseThrow(() -> new GameNotFoundException(gameId));
        Game updatedGame = gameToUpdate.withUpdatedScore(score);
        games.replace(gameId, updatedGame);
    }

    @Override
    public ScoreSummary getSummary() {
        List<Game> sortedGames = games.values()
                                      .stream()
                                      .sorted(gamesSortingComparator)
                                      .toList();
        return scoreSummaryFactory.createSummaryFor(sortedGames);
    }

}
