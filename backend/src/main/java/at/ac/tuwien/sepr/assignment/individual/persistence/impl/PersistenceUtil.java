package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;

@Component
public class PersistenceUtil {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  static void insertWithKeyHolder(KeyHolder keyHolder, String sqlInsert, MapSqlParameterSource parameterSource, NamedParameterJdbcTemplate jdbcNamed, Long id) {
    int rowsAffected = jdbcNamed.update(sqlInsert, parameterSource, keyHolder, new String[]{"id"});
    if (rowsAffected == 0) {
      String errorMessage = "Insert operation failed, no rows affected.";
      LOG.error(errorMessage);
      throw new FatalException(errorMessage);
    }
  }

}
