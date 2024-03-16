package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

/**
 * Service for working with tournaments
 */
public interface TournamentService {

  /**
   * Checks whether any of the tournaments has a horse with the given ID among its participants.
   *
   * @param horseId the ID of the horse to check for
   * @return {@code true} if the exists at least one such tournament.
   */
  boolean isHorseParticipantInAnyTournament(long horseId);

  /**
   * Create a new tournament. The tournament must have exactly 8 horses as participants.
   *
   * @param tournament the tournament to create
   * @return the created tournament
   * @throws ValidationException if the data given is in itself incorrect (no tournament name, name too long, ...)
   * @throws ConflictException   if the data given is in conflict with the data currently in the system (horses do not exist, ...)
   */
  TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException;
}
