package pl.wicherski.sportradar.scoreboard;

import java.util.Map;
import java.util.Objects;

class InMemoryLiveScoreBoard implements LiveScoreBoard {

    private final Map<GameId, Game> games;

    InMemoryLiveScoreBoard(Map<GameId, Game> games) {
        this.games = games;
    }

    @Override
    public GameId startGame(String homeTeam, String awayTeam) {
        if (Objects.isNull(homeTeam) || Objects.isNull(awayTeam)) {
            throw new IllegalArgumentException("Team name cannot be null! Home=%s, Away=%s".formatted(homeTeam,
                                                                                                      awayTeam));
        }
        Game game = new Game(homeTeam, awayTeam);
        GameId gameId = new GameId();
        games.put(gameId, game);
        return gameId;
    }

    @Override
    public void finishGame(GameId gameId) {

    }

    @Override
    public void updateScore(GameId gameId, Score score) throws GameNotFoundException {

    }

    @Override
    public ScoreSummary getSummary() {
        return null;
    }

}
