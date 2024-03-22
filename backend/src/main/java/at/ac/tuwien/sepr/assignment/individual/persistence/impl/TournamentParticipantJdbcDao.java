package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentParticipantDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;

@Repository
public class TournamentParticipantJdbcDao implements TournamentParticipantDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  private static final String TABLE_NAME = "participant";

  private static final String SQL_COUNT_PARTICIPANT_BY_HORSE_ID = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE horse_id = ?";

  private static final String NAMED_SQL_INSERT = "INSERT INTO " + TABLE_NAME
      + " (tournament_id, horse_id)"
      + " VALUES(:tournamentId, :horseId)";

  public TournamentParticipantJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public boolean isHorseParticipantInAnyTournament(long horseId) {
    LOG.trace("isHorseParticipantInAnyTournament({})", horseId);
    Integer count = jdbcTemplate.queryForObject(SQL_COUNT_PARTICIPANT_BY_HORSE_ID, Integer.class, horseId);
    return count != null && count > 0;
  }

  @Override
  public Participant create(ParticipantDto participantDto) {
    LOG.trace("Creating participant: {}", participantDto);
    var params = new BeanPropertySqlParameterSource(participantDto);
    int rowsAffected = jdbcNamed.update(NAMED_SQL_INSERT, params);
    if (rowsAffected == 0) {
      throw new FatalException("Error occurred during the insert operation");
    }

    return new Participant()
        .setTournamentId(participantDto.tournamentId())
        .setHorseId(participantDto.horseId());
  }

}
