package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Objects;

@Repository
public class TournamentJdbcDao implements TournamentDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  private static final String TABLE_NAME = "tournament";

  private static final String NAMED_SQL_INSERT_WITH_ID = "INSERT INTO " + TABLE_NAME
      + " (id, name, start_date, end_date, participants)"
      + " VALUES(:id, :name, :startData, :endDate, :participants)";


  private static final String NAMED_SQL_INSERT_WITHOUT_ID = "INSERT INTO " + TABLE_NAME
      + " (name, start_date, end_date, participants)"
      + " VALUES(:name, :startData, :endDate, :participants)";

  public TournamentJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate
  ) {
    this.jdbcNamed = jdbcNamed;
    this.jdbcTemplate = jdbcTemplate;
  }
  @Override
  public Collection<Tournament> search(TournamentSearchDto searchParameters) {
    return null;
  }

  @Override
  public Tournament create(TournamentDetailDto tournament) {
    LOG.trace("create({})", tournament);

    KeyHolder keyHolder = new GeneratedKeyHolder();   // KeyHolder for id of newly crated horse

    String sqlInsert = tournament.id() != null ? NAMED_SQL_INSERT_WITH_ID : NAMED_SQL_INSERT_WITHOUT_ID;   // ID optionally provided, e.g. during testing

    Long[] participantIds = tournament.participants().stream()
        .map(HorseDetailDto::id)
        .toArray(Long[]::new);

    MapSqlParameterSource parameterSource = new MapSqlParameterSource()
        .addValue("id", tournament.id())
        .addValue("name", tournament.name())
        .addValue("startData", tournament.startDate())
        .addValue("endDate", tournament.endDate())
        .addValue("participants", participantIds);

    try {
      int rowsAffected = jdbcNamed.update(sqlInsert, parameterSource, keyHolder, new String[]{"id"});

      if (rowsAffected == 0) {
        throw new FatalException("Insert operation failed, no rows affected.");
      }

      Long newId = (tournament.id() != null) ? tournament.id() : Objects.requireNonNull(keyHolder.getKey()).longValue();
      return new Tournament()
          .setId(newId)
          .setName(tournament.name())
          .setStartDate(tournament.startDate())
          .setEndDate(tournament.endDate())
          .setParticipantIds(participantIds)
          ;
    } catch (DataAccessException e) {
      throw new FatalException("Error occurred during the insert operation: " + e.getMessage(), e);
    }
  }

  @Override
  public Tournament getById(long id) throws NotFoundException {
    return null;
  }

  @Override
  public void delete(long id) throws NotFoundException {

  }

  private Tournament mapRow(ResultSet result, int rowNum) throws SQLException {
    Long[] participantArray = (Long[]) result.getArray("participants").getArray();

    return new Tournament()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setStartDate(result.getDate("start_date").toLocalDate())
        .setEndDate(result.getDate("end_data").toLocalDate())
        .setParticipantIds(participantArray)
        ;
  }
}
