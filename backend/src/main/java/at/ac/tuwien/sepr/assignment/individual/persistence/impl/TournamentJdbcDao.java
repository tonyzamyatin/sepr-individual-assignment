package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.lang.invoke.MethodHandles;
import java.util.Collection;

@Repository
public class TournamentJdbcDao implements TournamentDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private static String TABLE_NAME = "tournament";

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
  public Collection<Tournament> search(TournamentSearchDto searchParameters) {
    return null;
  }

  @Override
  public Tournament create(TournamentDetailDto tournament) {
    return null;
  }

  @Override
  public Tournament update(TournamentDetailDto tournament) throws NotFoundException {
    return null;
  }

  @Override
  public Tournament getById(long id) throws NotFoundException {
    return null;
  }

  @Override
  public void delete(long id) throws NotFoundException {

  }
}
