package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentMatchDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao tournamentDao;
  private final TournamentMatchDao matchDao;
  private final TournamentMapper mapper;
  private final TournamentValidator validator;
  private final HorseService horseService;

  public TournamentServiceImpl(TournamentDao tournamentDao, TournamentMatchDao matchDao, TournamentMapper mapper,
                               TournamentValidator validator, HorseService horseService) {
    this.tournamentDao = tournamentDao;
    this.matchDao = matchDao;
    this.mapper = mapper;
    this.validator = validator;
    this.horseService = horseService;
  }

  @Override
  public TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException {
    LOG.trace("create({})", tournament);
    validator.validateForCreate(tournament);
    var createdTournament = tournamentDao.create(tournament);
    var horses = horseMapForTournament(Arrays.stream(createdTournament.getParticipantIds()).collect(Collectors.toSet()));
    return mapper.entityToDetailDto(createdTournament, horses);
  }

  private Map<Long, HorseDetailDto> horseMapForTournament(Set<Long> participantIds) {

    Set<Long> nonNullHorseIds = participantIds.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    if (nonNullHorseIds.isEmpty()) {
      return Collections.emptyMap();
    }

    return horseService.findHorsesById(nonNullHorseIds)
        .collect(Collectors.toUnmodifiableMap(HorseDetailDto::id, Function.identity()));
  }

}
