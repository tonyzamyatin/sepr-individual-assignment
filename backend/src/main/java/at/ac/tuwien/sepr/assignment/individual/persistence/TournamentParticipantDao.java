package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;

/**
 * Data Access Object for tournament participants.
 * Implements access functionality to the application's persistent data store regarding tournament participants.
 */
public interface TournamentParticipantDao {

  /**
   * Add a participant to a tournament.
   *
   * @param participant the tournament participant to add
   * @throws NotFoundException if the tournament or horse does not exist
   */
  void addParticipant(TournamentParticipantDto participant) throws NotFoundException, ConflictException;

  /**
   * Get all participants of a specific tournament.
   *
   * @param tournamentId the ID of the tournament
   * @return a collection of tournament participants
   * @throws NotFoundException if the tournament with the given ID does not exist
   */
  Collection<TournamentParticipantDto> getParticipantsByTournament(long tournamentId) throws NotFoundException;

  /**
   * Remove a participant from a tournament.
   *
   * @param participant the tournament participant to remove
   * @throws NotFoundException if the tournament participant entry does not exist
   */
  void removeParticipant(TournamentParticipantDto participant) throws NotFoundException;

  /**
   * Check if a horse is a participant in a specific tournament.
   *
   * @param tournamentId the ID of the tournament
   * @param horseId the ID of the horse
   * @return true if the horse is a participant in the tournament, false otherwise
   */
  boolean isParticipant(long tournamentId, long horseId);

}
