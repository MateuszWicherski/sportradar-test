package pl.wicherski.sportradar.scoreboard;

class InMemoryLiveScoreBoard implements LiveScoreBoard {

    @Override
    public GameId startGame(String homeTeam, String awayTeam) {
        return new GameId();
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
