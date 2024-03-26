package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.List;

/**
 * Service for working with tournament participants.
 */
public interface TournamentParticipantService {
  /**
   * Checks whether any of the tournaments has a horse with the given ID among its participants.
   *
   * @param horseId the ID of the horse to check for
   * @return {@code true} if the exists at least one such tournament.
   */
  boolean isHorseParticipantInAnyTournament(long horseId);

  /**
   * Get all participants participating in the tournament with the given ID.
   *
   * @param tournamentId the ID of the tournament participation
   * @return List of the participants participating in the tournament, empty if no participants are found.
   */
  List<TournamentParticipantDetailDto> findParticipantsByTournamentId(long tournamentId);

  /**
   * Get the participant with the given horse ID participating in the tournament with the given tournament ID.
   *
   * @param tournamentId the ID of the tournament
   * @param horseId      the ID of the horse
   * @return the participants detail data
   * @throws NotFoundException if no participant with the given combination of IDs exists in the persistent data store.
   */
  TournamentParticipantDetailDto getParticipant(long tournamentId, long horseId) throws NotFoundException;

  /**
   * Create a new participant participating in the tournament with the given ID. The tournament entity must already exist in the system.
   *
   * @param tournamentId the ID of the tournament of participation
   * @param participant  the participant to create
   * @return the created participant
   * @throws ConflictException if the provided data for participant creation is in conflict with system data
   *                           (e.g. horse does not exist, etc.)
   */
  TournamentParticipantDetailDto create(long tournamentId, TournamentParticipantDetailDto participant) throws ConflictException;

  /**
   * Update the participant with the data given in {@code participant} which has the horse ID given in {@code participant} and participates
   * in the tournament with the ID {@code tournamentId}.
   *
   * @param tournamentId the ID of the participant's tournament.
   * @param participant  the participant to update
   * @return the updated participant.
   */
  TournamentParticipantDetailDto update(long tournamentId, TournamentParticipantDetailDto participant) throws NotFoundException;

}
