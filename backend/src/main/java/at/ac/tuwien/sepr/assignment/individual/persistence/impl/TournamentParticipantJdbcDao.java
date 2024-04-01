package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentParticipantDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class TournamentParticipantJdbcDao implements TournamentParticipantDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "participant";
  private static final String SQL_SELECT_PARTICIPANT = "SELECT 1 FROM " + TABLE_NAME + " WHERE tournament_id = ? AND horse_id = ?";
  private static final String SQL_PARTICIPANT_BY_TOURNAMENT_ID = "SELECT * FROM " + TABLE_NAME + " WHERE tournament_id = ?";
  private static final String SQL_COUNT_PARTICIPANT_BY_HORSE_ID = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE horse_id = ?";
  private static final String NAMED_SQL_INSERT = "INSERT INTO " + TABLE_NAME
      + " (tournament_id, horse_id, entry_number, round_reached)"
      + " VALUES(:tournamentId, :horseId, :entryNumber, :roundReached)";
  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET entry_number = ?"
      + " , round_reached = ?"
      + " WHERE tournament_id = ? AND horse_id = ?";
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentParticipantJdbcDao(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate jdbcNamed) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public List<Participant> findParticipantsByTournamentId(long tournamentId) {
    LOG.trace("findParticipantsByTournamentId({})", tournamentId);
    return jdbcTemplate.query(SQL_PARTICIPANT_BY_TOURNAMENT_ID, this::mapRow, tournamentId);
  }

  @Override
  public Participant getParticipant(long tournamentId, long horseId) throws NotFoundException {
    LOG.trace("getParticipant({}, {})", tournamentId, horseId);
    List<Participant> participants = jdbcTemplate.query(SQL_SELECT_PARTICIPANT, this::mapRow, tournamentId, horseId);
    if (participants.isEmpty()) {
      LOG.warn("Participant with tournament ID {} and horse ID {} not found", tournamentId, horseId);
      throw new NotFoundException("Participant with tournament ID " + tournamentId + " and horse ID " + horseId + " does not exist");
    }
    return participants.getFirst();
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
    try {
      int rowsAffected = jdbcNamed.update(NAMED_SQL_INSERT, params);
      if (rowsAffected == 0) {
        String errorMessage = "Insert operation failed, no rows affected.";
        LOG.error(errorMessage);
        throw new FatalException(errorMessage);
      }
    } catch (DataAccessException e) {
      String errorMessage = "Unexpected error during insert into the participant table: " + e.getMessage();
      LOG.error(errorMessage, e);
      throw new FatalException(errorMessage, e);
    }

    return new Participant()
        .setTournamentId(tournamentId)
        .setHorseId(participant.horseId())
        .setEntryNumber(participant.entryNumber())
        .setRoundReached(participant.roundReached());
  }

  @Override
  public Participant update(long tournamentId, TournamentParticipantDetailDto participant) {
    LOG.trace("Updating participant: {}", participant);
    try {
      int updated = jdbcTemplate.update(SQL_UPDATE,
          participant.entryNumber(),
          participant.roundReached(),
          tournamentId,
          participant.horseId());
      if (updated == 0) {
        LOG.warn("No participants where updated");
      }
    } catch (DataAccessException e) {
      String errorMessage = "Unexpected error during update of participant table: " + e.getMessage();
      LOG.error(errorMessage, e);
      throw new FatalException(errorMessage, e);
    }

    return new Participant()
        .setTournamentId(tournamentId)
        .setHorseId(participant.horseId())
        .setEntryNumber(participant.entryNumber())
        .setRoundReached(participant.roundReached());
  }

  private Participant mapRow(ResultSet result, int rowNumber) throws SQLException {
    return new Participant()
        .setTournamentId(result.getLong("tournament_id"))
        .setHorseId(result.getLong("horse_id"))
        .setEntryNumber(result.getInt("entry_number"))
        .setRoundReached(result.getInt("round_reached"));
  }

}
