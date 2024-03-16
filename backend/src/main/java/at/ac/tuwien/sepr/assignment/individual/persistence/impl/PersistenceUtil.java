package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;

public class PersistenceUtil {

  static void insertWithKeyHolder(KeyHolder keyHolder, String sqlInsert, MapSqlParameterSource parameterSource, NamedParameterJdbcTemplate jdbcNamed, Long id) {
    int rowsAffected = jdbcNamed.update(sqlInsert, parameterSource, keyHolder, new String[]{"id"});
    if (rowsAffected == 0) {
      throw new FatalException("Insert operation failed, no rows affected.");
    }
  }

}
