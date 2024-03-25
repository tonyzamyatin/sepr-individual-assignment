package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentParticipantDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
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
      + " (tournament_id, horse_id, entry_number, round_reached)"
      + " VALUES(:tournamentId, :horseId, :entryNumber, :roundReached)";

  public TournamentParticipantJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public boolean isHorseParticipantInAnyTournament(long horseId) {
    LOG.trace("isHorseParticipantInAnyTournament({})", horseId);
    Integer count = jdbcTemplate.queryForObject(SQL_COUNT_PARTICIPANT_BY_HORSE_ID, Integer.class, horseId);
    LOG.debug("Horse with id " + horseId + " participates in " + count + " tournaments");
    return count != null && count > 0;
  }

  @Override
  public Participant create(long tournamentId, TournamentParticipantDetailDto participant) {
    LOG.trace("Creating participant: {}", participant);
    var params = new MapSqlParameterSource()
        .addValue("tournamentId", tournamentId)
        .addValue("horseId", participant.horseId())
        .addValue("entryNumber", participant.entryNumber())
        .addValue("roundReached", participant.roundReached());
    int rowsAffected = jdbcNamed.update(NAMED_SQL_INSERT, params);
    if (rowsAffected == 0) {
      throw new FatalException("Error occurred during the insert operation");
    }

    return new Participant()
        .setTournamentId(tournamentId)
        .setHorseId(participant.horseId())
        .setEntryNumber(participant.entryNumber())
        .setRoundReached(participant.roundReached());
  }

}
