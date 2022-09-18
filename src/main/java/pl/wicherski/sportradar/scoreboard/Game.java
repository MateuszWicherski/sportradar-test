package pl.wicherski.sportradar.scoreboard;

public record Game(String homeTeamName, String awayTeamName, Score score) {

    public Game withUpdatedScore(Score score) {
        return new Game(homeTeamName, awayTeamName, score);
    }

}
