package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;

public interface TournamentParticipantDao {

  /**
   * Get all participants participating in the tournament with the given ID.
   *
   * @param tournamentId the ID of the tournament participation
   * @return the participants participating part in the tournament
   */
  Collection<Participant> findParticipantsByTournamentId(long tournamentId);

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
   * @param participant  the participant to be created containing the id of the horse.
   * @return the newly created participant
   */
  Participant create(long tournamentId, TournamentParticipantDetailDto participant);

  /**
   * Updates the participant participating in the tournament with ID {@code tournamentId}
   * and the horse ID in {@code participant} with the data in {@code participant}.
   *
   * @param tournamentId the ID of the tournament of participation.
   * @param participant  the participant to be updated containing the ID of the horse.
   * @return the updated created participant
   */
  Participant update(long tournamentId, TournamentParticipantDetailDto participant) throws NotFoundException;
}
