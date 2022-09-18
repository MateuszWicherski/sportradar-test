package pl.wicherski.sportradar.scoreboard;

import java.time.Instant;

record Game(String homeTeamName, String awayTeamName, Score score, Instant creationTimestamp) {

    public Game withUpdatedScore(Score score) {
        return new Game(homeTeamName, awayTeamName, score, creationTimestamp);
    }

}
