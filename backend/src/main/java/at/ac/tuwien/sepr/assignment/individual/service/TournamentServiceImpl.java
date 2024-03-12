package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentMatchDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentParticipantDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;

@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao tournamentDao;
  private final TournamentParticipantDao participantDao;
  private final TournamentMatchDao matchDao;
  private final TournamentMapper mapper;
  private final TournamentValidator validator;

  public TournamentServiceImpl(TournamentDao tournamentDao, TournamentParticipantDao participantDao, TournamentMatchDao matchDao, TournamentMapper mapper,
                               TournamentValidator validator) {
    this.tournamentDao = tournamentDao;
    this.participantDao = participantDao;
    this.matchDao = matchDao;
    this.mapper = mapper;
    this.validator = validator;
  }

  @Override
  public TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException {
    return null;
  }
}
