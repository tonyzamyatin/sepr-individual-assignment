package at.ac.tuwien.sepr.assignment.individual.entity;


/**
 * Represents a tournament participant in the persistent data store.
 */
public class TournamentParticipant {
  private Long tournamentId;
  private Long horseId;

  public Long getTournamentId() {
    return tournamentId;
  }

  public Long getHorseId() {
    return horseId;
  }

  @Override
  public String toString() {
    return "TournamentParticipant{" +
        "tournamentId=" + tournamentId +
        ", horseId=" + horseId +
        '}';
  }
}
