package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

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
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static at.ac.tuwien.sepr.assignment.individual.persistence.impl.PersistenceUtil.insertWithKeyHolder;

@Repository
public class TournamentJdbcDao implements TournamentDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static final String TABLE_NAME = "tournament";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";
  private static final String SQL_SELECT_SEARCH = "SELECT * "
      + " FROM " + TABLE_NAME
      + " WHERE (:name IS NULL OR UPPER(name) LIKE UPPER('%'||:name||'%'))"
      + "  AND (:intervalStart IS NULL OR :intervalStart <= end_date)"
      + "  AND (:intervalEnd IS NULL OR :intervalEnd >= start_date)";
  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";
  private static final String SQL_COUNT_THAT_CONTAIN_PARTICIPANT = "SELECT COUNT(*) FROM " + TABLE_NAME + " WHERE ARRAY_CONTAINS(participants, :horseId)";
  private static final String NAMED_SQL_INSERT_WITH_ID = "INSERT INTO " + TABLE_NAME
      + " (id, name, start_date, end_date)"
      + " VALUES(:id, :name, :startDate, :endDate)";
  private static final String NAMED_SQL_INSERT_WITHOUT_ID = "INSERT INTO " + TABLE_NAME
      + " (name, start_date, end_date)"
      + " VALUES(:name, :startDate, :endDate)";
  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;

  public TournamentJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate
  ) {
    this.jdbcNamed = jdbcNamed;
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Tournament getById(long id) throws NotFoundException {
    LOG.trace("getTournament({})", id);
    List<Tournament> tournaments;
    tournaments = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (tournaments.isEmpty()) {
      LOG.warn("No tournament with ID {} found", id);
      throw new NotFoundException("No tournament with ID %d found".formatted(id));
    }
    if (tournaments.size() > 1) {
      // This should never happen!!
      String errorMessage = "Too many tournaments with ID %d found".formatted(id);
      LOG.error("Unexpected error during retrieval of tournament by id: {}", errorMessage);
      throw new FatalException(errorMessage);
    }

    return tournaments.getFirst();
  }

  @Override
  public Collection<Tournament> search(TournamentSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    if (searchParameters.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }
    var params = new BeanPropertySqlParameterSource(searchParameters);
    return jdbcNamed.query(query, params, this::mapRow);
  }

  @Override
  public boolean isHorseParticipantInAnyTournament(long horseId) {
    LOG.trace("isHorseParticipantInAnyTournament({})", horseId);
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("horseId", horseId);
    Integer count = jdbcNamed.queryForObject(SQL_COUNT_THAT_CONTAIN_PARTICIPANT, params, Integer.class);
    return count != null && count > 0;
  }

  @Override
  public Tournament create(TournamentDetailDto tournament) {
    LOG.trace("create({})", tournament);

    KeyHolder keyHolder = new GeneratedKeyHolder();   // KeyHolder for id of newly crated horse

    String sqlInsert = tournament.id() != null ? NAMED_SQL_INSERT_WITH_ID : NAMED_SQL_INSERT_WITHOUT_ID;   // ID optionally provided, e.g. during testing

    MapSqlParameterSource parameterSource = new MapSqlParameterSource()
        .addValue("id", tournament.id())
        .addValue("name", tournament.name())
        .addValue("startDate", tournament.startDate())
        .addValue("endDate", tournament.endDate());
    try {
      insertWithKeyHolder(keyHolder, sqlInsert, parameterSource, jdbcNamed, tournament.id());
    } catch (DataAccessException e) {
      String errorMessage = "Unexpected error error during insert into tournament table: " + e.getMessage();
      LOG.error(errorMessage, e);
      throw new FatalException(errorMessage, e);
    }

    Long newId = (tournament.id() != null) ? tournament.id() : Objects.requireNonNull(keyHolder.getKey()).longValue();
    return new Tournament()
        .setId(newId)
        .setName(tournament.name())
        .setStartDate(tournament.startDate())
        .setEndDate(tournament.endDate());
  }

  private Tournament mapRow(ResultSet result, int rowNum) throws SQLException {
    return new Tournament()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setStartDate(result.getDate("start_date").toLocalDate())
        .setEndDate(result.getDate("end_date").toLocalDate());
  }
}