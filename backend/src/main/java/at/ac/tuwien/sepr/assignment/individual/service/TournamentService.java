package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

/**
 * Service for working with tournaments
 */
public interface TournamentService {

  /**
   * Create a new tournament. The tournament must have exactly 8 horses as participants.
   * @param tournament the tournament to create
   * @return the created tournament
   * @throws ValidationException if the data given is in itself incorrect (no tournament name, name too long, ...)
   * @throws ConflictException if the data given is in conflict with the data currently in the system (horses do not exist, ...)
   */
  TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException;
}
