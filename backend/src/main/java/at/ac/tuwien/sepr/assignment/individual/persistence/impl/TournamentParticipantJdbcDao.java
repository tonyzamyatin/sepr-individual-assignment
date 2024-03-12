package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentParticipantDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

@Repository
public class TournamentParticipantJdbcDao implements TournamentParticipantDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static String TABLE_NAME = "tournament_participant";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentParticipantJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate
  ) {
    this.jdbcNamed = jdbcNamed;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addParticipant(TournamentParticipantDto participant) throws NotFoundException, ConflictException {

  }

  @Override
  public Collection<TournamentParticipantDto> getParticipantsByTournament(long tournamentId) throws NotFoundException {
    return null;
  }

  @Override
  public void removeParticipant(TournamentParticipantDto participant) throws NotFoundException {

  }

  @Override
  public boolean isParticipant(long tournamentId, long horseId) {
    return false;
  }
}
