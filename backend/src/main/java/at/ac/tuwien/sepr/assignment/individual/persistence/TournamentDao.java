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
   * Checks whether there exists a tournament containing a horse specified by the given ID among its participants.
   *
   * @param horseId the ID of the horse to check for
   * @return {@code true} if the given ID is found among the participants in at least one tournament.
   */
  boolean isHorseParticipantInAnyTournament(long horseId);

  /**
   * Create a new tournament with the data given in {@code tournament}.
   *
   * @param tournament the new tournament to create
   * @return the newly created tournament
   */
  Tournament create(TournamentDetailDto tournament);
}
