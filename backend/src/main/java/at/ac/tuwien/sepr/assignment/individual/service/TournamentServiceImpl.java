package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao tournamentDao;
  private final TournamentMatchDao matchDao;
  private final TournamentParticipantDao participantDao;
  private final TournamentMapper mapper;
  private final TournamentValidator validator;
  private final HorseService horseService;

  public TournamentServiceImpl(TournamentDao tournamentDao, TournamentMatchDao matchDao, TournamentParticipantDao participantDao, TournamentMapper mapper,
                               TournamentValidator validator, HorseService horseService) {
    this.tournamentDao = tournamentDao;
    this.matchDao = matchDao;
    this.participantDao = participantDao;
    this.mapper = mapper;
    this.validator = validator;
    this.horseService = horseService;
  }

  @Override
  public boolean isHorseParticipantInAnyTournament(long horseId) {
    LOG.trace("isHorseParticipantInAnyTournament({})", horseId);
    return participantDao.isHorseParticipantInAnyTournament(horseId);
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchParameters) {
    var tournaments = tournamentDao.search(searchParameters);
    return tournaments.stream().map(mapper::entityToListDto);
  }

  @Override
  public TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException {
    LOG.trace("create({})", tournament);
    validator.validateForCreate(tournament);
    // Create tournament
    var createdTournament = tournamentDao.create(tournament);
    // Create participants
    List<Participant> participants = tournament.participants()
        .stream()
        .map(participantDetailDto -> participantDao.create(tournament.id(), participantDetailDto))
        .toList();
    Set<Long> participantIds = participants.stream()
        .map(Participant::getHorseId)
        .collect(Collectors.toSet());
    var horseMap = horseMapForTournament(participantIds);
    return mapper.entityToDetailDto(createdTournament, participants, horseMap);
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
