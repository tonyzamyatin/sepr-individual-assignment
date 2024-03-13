package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.dao.DataAccessException;


@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";
  private static final String SQL_SELECT_BY_ID = "SELECT * FROM " + TABLE_NAME + " WHERE id = ?";

  private static final String SQL_FIND_BY_IDS =
      "SELECT * FROM " + TABLE_NAME
          + " WHERE id IN (:ids)";

  private static final String SQL_SELECT_SEARCH = "SELECT  "
          + "    h.id as \"id\", h.name as \"name\", h.sex as \"sex\", h.date_of_birth as \"date_of_birth\""
          + "    , h.height as \"height\", h.weight as \"weight\", h.breed_id as \"breed_id\""
          + " FROM " + TABLE_NAME + " h LEFT OUTER JOIN breed b ON (h.breed_id = b.id)"
          + " WHERE (:name IS NULL OR UPPER(h.name) LIKE UPPER('%'||:name||'%'))"
          + "  AND (:sex IS NULL OR :sex = sex)"
          + "  AND (:bornEarliest IS NULL OR :bornEarliest <= h.date_of_birth)"
          + "  AND (:bornLatest IS NULL OR :bornLatest >= h.date_of_birth)"
          + "  AND (:breed IS NULL OR UPPER(b.name) LIKE UPPER('%'||:breed||'%'))";


  private static final String SQL_LIMIT_CLAUSE = " LIMIT :limit";

  private static final String SQL_UPDATE = "UPDATE " + TABLE_NAME
      + " SET name = ?"
      + "  , sex = ?"
      + "  , date_of_birth = ?"
      + "  , height = ?"
      + "  , weight = ?"
      + "  , breed_id = ?"
      + " WHERE id = ?";

  private static final String NAMED_SQL_INSERT_WITH_ID = "INSERT INTO " + TABLE_NAME
      + " (id, name, sex, date_of_birth, height, weight, breed_id)"
      + " VALUES(:id, :name, :sex, :dateOfBirth, :height, :weight, :breedId)";


  private static final String NAMED_SQL_INSERT_WITHOUT_ID = "INSERT INTO " + TABLE_NAME
      + " (name, sex, date_of_birth, height, weight, breed_id)"
      + " VALUES(:name, :sex, :dateOfBirth, :height, :weight, :breedId)";

  private final JdbcTemplate jdbcTemplate;
  private final NamedParameterJdbcTemplate jdbcNamed;


  public HorseJdbcDao(
      NamedParameterJdbcTemplate jdbcNamed,
      JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.jdbcNamed = jdbcNamed;
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses;
    horses = jdbcTemplate.query(SQL_SELECT_BY_ID, this::mapRow, id);

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.getFirst();
  }


  @Override
  public Collection<Horse> findHorsesById(Set<Long> horseIds) {
    LOG.trace("findHorsesById({})", horseIds);
    return jdbcNamed.query(SQL_FIND_BY_IDS, Map.of("ids", horseIds), this::mapRow);
  }


  @Override
  public void delete(Long id) throws NotFoundException, ConflictException {
    // TODO: Implement
  }


  @Override
  public Collection<Horse> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var query = SQL_SELECT_SEARCH;
    if (searchParameters.limit() != null) {
      query += SQL_LIMIT_CLAUSE;
    }
    var params = new BeanPropertySqlParameterSource(searchParameters);
    params.registerSqlType("sex", Types.VARCHAR);

    return jdbcNamed.query(query, params, this::mapRow);
  }


  @Override
  public Horse create(HorseDetailDto horse) {
    LOG.trace("create({})", horse);

    KeyHolder keyHolder = new GeneratedKeyHolder();   // KeyHolder for id of newly crated horse

    String sqlInsert = horse.id() != null ? NAMED_SQL_INSERT_WITH_ID : NAMED_SQL_INSERT_WITHOUT_ID;   // ID optionally provided, e.g. during testing

    // Handle optional breedId
    Long optionalBreedId = horse.breed() != null ? horse.breed().id() : null;

    MapSqlParameterSource parameterSource = new MapSqlParameterSource()
        .addValue("id", horse.id())
        .addValue("name", horse.name())
        .addValue("sex", horse.sex().toString())
        .addValue("dateOfBirth", horse.dateOfBirth())
        .addValue("height", horse.height())
        .addValue("weight", horse.weight())
        .addValue("breedId", optionalBreedId); // Extracting breedId from BreedDto

    try {
      int rowsAffected = jdbcNamed.update(sqlInsert, parameterSource, keyHolder, new String[]{"id"});

      if (rowsAffected == 0) {
        throw new FatalException("Insert operation failed, no rows affected.");
      }

      Long newId = (horse.id() != null) ? horse.id() : Objects.requireNonNull(keyHolder.getKey()).longValue();
      return new Horse()
          .setId(newId)
          .setName(horse.name())
          .setSex(horse.sex())
          .setDateOfBirth(horse.dateOfBirth())
          .setHeight(horse.height())
          .setWeight(horse.weight())
          .setBreedId(optionalBreedId)
          ;
    } catch (DataAccessException e) {
      throw new FatalException("Error occurred during the insert operation: " + e.getMessage(), e);
    }
  }


  @Override
  public Horse update(HorseDetailDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);

    // Handle optional breedId
    Long optionalBreedId = horse.breed() != null ? horse.breed().id() : null;

    int updated = jdbcTemplate.update(SQL_UPDATE,
        horse.name(),
        horse.sex().toString(),
        horse.dateOfBirth(),
        horse.height(),
        horse.weight(),
        optionalBreedId,
        horse.id());
    if (updated == 0) {
      throw new NotFoundException("Could not update horse with ID " + horse.id() + ", because it does not exist");
    }

    return new Horse()
        .setId(horse.id())
        .setName(horse.name())
        .setSex(horse.sex())
        .setDateOfBirth(horse.dateOfBirth())
        .setHeight(horse.height())
        .setWeight(horse.weight())
        .setBreedId(optionalBreedId)
        ;
  }


  private Horse mapRow(ResultSet result, int rowNum) throws SQLException {
    return new Horse()
        .setId(result.getLong("id"))
        .setName(result.getString("name"))
        .setSex(Sex.valueOf(result.getString("sex")))
        .setDateOfBirth(result.getDate("date_of_birth").toLocalDate())
        .setHeight(result.getFloat("height"))
        .setWeight(result.getFloat("weight"))
        .setBreedId(result.getObject("breed_id", Long.class))
        ;
  }
}
