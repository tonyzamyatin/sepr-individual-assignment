package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;

/**
 * Data Access Object for tournaments.
 * Implements access functionality to the application's persistent data store regarding tournaments.
 */
public interface TournamentDao {

  /**
   * Get the tournaments that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in tournament.
   *
   * @param searchParameters the parameters to use in searching.
   * @return the tournaments where all given parameters match.
   */
  Collection<Tournament> search(TournamentSearchDto searchParameters);

  /**
   * Create a new tournament with the data given in {@code tournament}.
   *
   * @param tournament the new tournament to create
   * @return the newly created tournament
   */
  Tournament create(TournamentDetailDto tournament);


  /**
   * Get a tournament by its ID from the persistent data store.
   *
   * @param id the ID of the tournament to get
   * @return the tournament
   * @throws NotFoundException if the Tournament with the given ID does not exist in the persistent data store
   */
  Tournament getById(long id) throws NotFoundException;

  /**
   * Delete a tournament by its ID from the persistent data store.
   *
   * @param id the ID of the tournament to delete
   * @throws NotFoundException if the tournament with the given ID does not exist in the persistent data store
   */
  void delete(long id) throws NotFoundException;

}
