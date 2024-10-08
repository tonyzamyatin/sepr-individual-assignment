package at.ac.tuwien.sepr.assignment.individual.entity;

/**
 * Represents a match-up in a tournament in the persistent data store.
 */
public class TournamentMatch {
  private Long matchId;
  private Long tournamentId;
  private Long horse1Id;
  private Long horse2Id;
  private int roundNumber;
  private int matchNumber;
  private Long winnerHorseId; // Can be null if the match hasn't been played yet

  public Long getMatchId() {
    return matchId;
  }

  public TournamentMatch setMatchId(Long matchId) {
    this.matchId = matchId;
    return this;
  }

  public Long getTournamentId() {
    return tournamentId;
  }

  public TournamentMatch setTournamentId(Long tournamentId) {
    this.tournamentId = tournamentId;
    return this;
  }

  public Long getHorse1Id() {
    return horse1Id;
  }

  public TournamentMatch setHorse1Id(Long horse1Id) {
    this.horse1Id = horse1Id;
    return this;
  }

  public Long getHorse2Id() {
    return horse2Id;
  }

  public TournamentMatch setHorse2Id(Long horse2Id) {
    this.horse2Id = horse2Id;
    return this;
  }

  public Integer getRoundNumber() {
    return roundNumber;
  }

  public TournamentMatch setRoundNumber(Integer roundNumber) {
    this.roundNumber = roundNumber;
    return this;
  }

  public Integer getMatchNumber() {
    return matchNumber;
  }

  public TournamentMatch setMatchNumber(Integer matchNumber) {
    this.matchNumber = matchNumber;
    return this;
  }

  public Long getWinnerHorseId() {
    return winnerHorseId;
  }

  public TournamentMatch setWinnerHorseId(Long winnerHorseId) {
    this.winnerHorseId = winnerHorseId;
    return this;
  }

  @Override
  public String toString() {
    return "TournamentMatchUp{"
        + "matchId=" + matchId
        + ", tournamentId=" + tournamentId
        + ", horse1Id=" + horse1Id
        + ", horse2Id=" + horse2Id
        + ", roundNumber=" + roundNumber
        + ", matchNumber=" + matchNumber
        + ", winnerHorseId=" + winnerHorseId
        + '}';
  }
}
