package pl.wicherski.sportradar.scoreboard;

/**
 * Interface for snapshot of the scores. Once created, summary won't be updated.
 */
public interface ScoreSummary {

    /**
     * Returns printable summary of the scores. The format is dependent on the implementation.
     *
     * @return score summary as {@link String}
     */
    String toPrintableSummary();

}
