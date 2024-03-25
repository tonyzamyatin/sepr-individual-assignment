package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;

public interface TournamentParticipantDao {
  /**
   * Checks whether the horse with the given id participates in any tournaments.
   *
   * @param horseId the ID of the horse to check for
   * @return {@code true} if the given ID is found among the participants in at least one tournament.
   */
  boolean isHorseParticipantInAnyTournament(long horseId);

  /**
   * Creates a new participant in a tournament.
   *
   * @param tournamentId the id of the tournament of participation.
   * @param participant the participant to be created containing the id of the horse.
   * @return the newly created participant
   */
  Participant create(long tournamentId, TournamentParticipantDetailDto participant);
}
