package pl.wicherski.sportradar.scoreboard;

public record Score(int home, int away) {

    public Score {
        if (home < 0 || away < 0) {
            throw new IllegalArgumentException("Score cannot be negative. Home=%s, Away=%s".formatted(home, away));
        }
    }

    public static Score of(int home, int away) {
        return new Score(home, away);
    }

}
