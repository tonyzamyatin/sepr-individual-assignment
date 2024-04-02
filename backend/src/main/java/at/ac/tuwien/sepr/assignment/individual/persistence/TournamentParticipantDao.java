package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.List;

public interface TournamentParticipantDao {

  /**
   * Get all participants participating in the tournament with the given ID.
   *
   * @param tournamentId the ID of the tournament participation
   * @return List of the participants participating part in the tournament, empty if no participants are found.
   */
  List<Participant> findParticipantsByTournamentId(long tournamentId);

  /**
   * Search for participants in the persistent data store based on the given search parameters.
   * Parameters that are {@code null} are ignored.
   * If all parameters are {@code null}, all participants are returned.
   *
   * @param searchParams the search parameters to search for participants
   * @return List of participants matching the search parameters, empty if no participants are found.
   */
  List<Participant> search(ParticipantSearchDto searchParams);

  /**
   * Retrieve the participant with the given horse ID participating in the tournament with the given tournament ID from the persistent data store.
   *
   * @param tournamentId the ID of the tournament
   * @param horseId  the ID of the horse
   * @return the participant with the given IDs
   * @throws NotFoundException if no participant with the given combination of IDs exists in the persistent data store.
   */
  Participant getParticipant(long tournamentId, long horseId) throws NotFoundException;

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
  Participant create(long tournamentId, ParticipantDetailDto participant);

  /**
   * Updates the participant participating in the tournament with ID {@code tournamentId}
   * and the horse ID in {@code participant} with the data in {@code participant}.
   *
   * @param tournamentId the ID of the tournament of participation.
   * @param participant  the participant to be updated containing the ID of the horse.
   * @return the updated created participant
   */
  Participant update(long tournamentId, ParticipantDetailDto participant);
}
