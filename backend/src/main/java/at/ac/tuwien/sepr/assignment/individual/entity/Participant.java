package at.ac.tuwien.sepr.assignment.individual.entity;

/**
 * Represents a horse in the persistent data store.
 */
public class Participant {
  private Long tournamentId;
  private Long horseId;
  private Integer entryNumber;
  private Integer roundReached;


  public Long getTournamentId() {
    return tournamentId;
  }

  public Participant setTournamentId(Long tournamentId) {
    this.tournamentId = tournamentId;
    return this;
  }

  public Long getHorseId() {
    return horseId;
  }

  public Participant setHorseId(Long horseId) {
    this.horseId = horseId;
    return this;
  }

  public Integer getEntryNumber() {
    return entryNumber;
  }

  public Participant setEntryNumber(Integer entryNumber) {
    this.entryNumber = entryNumber;
    return this;
  }

  public Integer getRoundReached() {
    return roundReached;
  }

  public Participant setRoundReached(Integer roundReached) {
    this.roundReached = roundReached;
    return this;
  }

  @Override
  public String toString() {
    return "Participant{"
        + "tournamentId=" + tournamentId
        + ", horseId=" + horseId
        + '}';
  }
}
