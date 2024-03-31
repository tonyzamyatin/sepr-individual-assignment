package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.stream.Stream;

/**
 * Service for working with tournaments
 */
public interface TournamentService {

  /**
   * Get the tournament with given ID, with more detail information, including a list of the participants.
   *
   * @param id the ID of the tournament to get
   * @return the tournament with ID {@code id}
   * @throws NotFoundException if the tournament with the given ID does not exist in the persistent data
   *                           store
   */
  TournamentDetailDto getTournament(long id) throws NotFoundException;

  /**
   * Checks whether a tournament with the given ID exists in the persistent data store.
   *
   * @param id the tournament ID of the tournament in question
   * @return true if the tournament exists, else false
   */
  boolean doesTournamentExist(long id);

  /**
   * Search for tournaments in the persistent data store matching all provided fields. The tournament name is
   * considered a match, if the search string is a substring of the field in horse. The tournament period
   * is considered a match if the tournament has at least one overlapping day with the specified time
   * interval.
   *
   * @param searchParameters the search parameters to use in filtering.
   * @return the horses where the given fields match.
   */
  Stream<TournamentListDto> search(TournamentSearchDto searchParameters);

  /**
   * Create a new tournament. The tournament must have exactly 8 horses as participants.
   *
   * @param tournament the tournament to create
   * @return the created tournament
   * @throws ValidationException if the data given is in itself incorrect (no tournament name, name too long, ...)
   * @throws ConflictException   if the data given is in conflict with the data currently in the system (horses do not exist, ...)
   */
  TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException;

  /**
   * Retrieves the standings of the tournament with the given id from the persistent data store.
   *
   * @param id the id of the tournament
   * @return the standings of the tournament
   * @throws NotFoundException if the tournament with the specified id does not exist.
   */
  TournamentStandingsDto getStandings(long id) throws NotFoundException;

  /**
   * Update the standings of the tournament with ID given in {@code tournamentStandings}.
   *
   * @param tournamentStandings the tournament standings to update, containing tournament ID, name, participants, and standings tree.
   * @return the updated standings of the tournament
   * @throws ConflictException if the tournament with the specified id does not exist.
   */
  TournamentStandingsDto updateStandings(TournamentStandingsDto tournamentStandings) throws ConflictException, ValidationException, NotFoundException;
}
