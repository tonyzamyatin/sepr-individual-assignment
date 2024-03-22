package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;

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
   * @param participant the participant to be created, containing the ids of the tournament and horse.
   * @return the newly created participant
   */
  Participant create(ParticipantDto participant);
}
