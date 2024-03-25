package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.ParticipantMapper;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentMatchDao;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentParticipantDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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
  private final TournamentMapper tournamentMapper;
  private final ParticipantMapper participantMapper;
  private final TournamentValidator validator;
  private final HorseService horseService;

  public TournamentServiceImpl(TournamentDao tournamentDao, TournamentMatchDao matchDao, TournamentParticipantDao participantDao,
                               TournamentMapper tournamentMapper,
                               ParticipantMapper participantMapper,
                               TournamentValidator validator, HorseService horseService) {
    this.tournamentDao = tournamentDao;
    this.matchDao = matchDao;
    this.participantDao = participantDao;
    this.tournamentMapper = tournamentMapper;
    this.participantMapper = participantMapper;
    this.validator = validator;
    this.horseService = horseService;
  }

  @Override
  public TournamentDetailDto getById(long id) throws NotFoundException {
    Tournament tournament = tournamentDao.getById(id);
    List<Participant> participants = participantDao.findParticipantsByTournamentId(id)
        .stream()
        .sorted(Comparator.comparingInt(Participant::getEntryNumber))
        .toList();
    Set<Long> participantIds = participants.stream()
        .map(Participant::getHorseId)
        .collect(Collectors.toSet());
    var horseMap = horseMapForTournament(participantIds);
    return tournamentMapper.entityToDetailDto(tournament, participants, horseMap);
  }

  @Override
  public boolean isHorseParticipantInAnyTournament(long horseId) {
    LOG.trace("isHorseParticipantInAnyTournament({})", horseId);
    return participantDao.isHorseParticipantInAnyTournament(horseId);
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchParameters) {
    var tournaments = tournamentDao.search(searchParameters);
    return tournaments.stream().map(tournamentMapper::entityToListDto);
  }

  @Override
  public TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException {
    LOG.trace("create({})", tournament);
    validator.validateForCreate(tournament);
    // Create tournament
    Tournament createdTournament = tournamentDao.create(tournament);
    // Create participants
    List<Participant> participants = tournament.participants()
        .stream()
        .map(participantDetailDto -> participantDao.create(tournament.id(), participantDetailDto))
        .toList();
    Set<Long> participantIds = participants.stream()
        .map(Participant::getHorseId)
        .collect(Collectors.toSet());
    var horseMap = horseMapForTournament(participantIds);
    return tournamentMapper.entityToDetailDto(createdTournament, participants, horseMap);
  }

  @Override
  public TournamentStandingsDto getStandings(long id) throws NotFoundException {
    LOG.trace("getStandings({})", id);
    Tournament tournament = tournamentDao.getById(id);
    List<Participant> participants = participantDao.findParticipantsByTournamentId(id).stream().toList();
    Set<Long> participantIds = participants.stream()
        .map(Participant::getHorseId)
        .collect(Collectors.toSet());
    Map<Long, HorseDetailDto> horseMap = horseMapForTournament(participantIds);
    List<TournamentParticipantDetailDto> participantDetailDtoList = participants.stream()
        .map(participant -> participantMapper.entityToDetailDto(participant, horseMap))
        .toList();
    return new TournamentStandingsDto(
        tournament.getId(),
        tournament.getName(),
        participantDetailDtoList,
        buildStandingsTree(participantDetailDtoList, 3)
    );
  }

  @Override
  public TournamentStandingsDto updateStandings(long id, TournamentStandingsDto tournamentStandings)
      throws ConflictException, ValidationException, NotFoundException {
    LOG.trace("updateStandings({}, {})", id, tournamentStandings);
    validator.validateForStandingsUpdate(id, tournamentStandings);
    Map<Long, Integer> roundReachedMap = new HashMap<>();
    encodeStandingsTreeIntoMap(roundReachedMap, tournamentStandings.tree(), 3, 0);
    List<Participant> participants = tournamentStandings.participants()
        .stream()
        .map(participantDetailDto -> {
          try {
            return participantDao.update(id, new TournamentParticipantDetailDto(
                participantDetailDto.horseId(),
                participantDetailDto.name(),
                participantDetailDto.dateOfBirth(),
                participantDetailDto.entryNumber(),
                roundReachedMap.get(participantDetailDto.horseId())
            ));
          } catch (NotFoundException e) {
            throw new FatalException(e);
          }
        })
        .toList();
    Set<Long> participantIds = participants.stream()
        .map(Participant::getHorseId)
        .collect(Collectors.toSet());
    Map<Long, HorseDetailDto> horseMap = horseMapForTournament(participantIds);
    List<TournamentParticipantDetailDto> participantDetailDtoList = participants.stream()
        .map(participant -> participantMapper.entityToDetailDto(participant, horseMap))
        .toList();
    return new TournamentStandingsDto(
        tournamentStandings.id(),
        tournamentStandings.name(),
        participantDetailDtoList,
        buildStandingsTree(participantDetailDtoList, 3)
    );
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

  private void encodeStandingsTreeIntoMap(Map<Long, Integer> roundsReachedMap, TournamentStandingsTreeDto standingsTree, int maxRounds, int depth) {
    if (maxRounds - depth < 0) {  // or branches == null as break criterion
      return;
    }
    var participant = standingsTree.thisParticipant();
    if (participant != null && !roundsReachedMap.containsKey(participant.horseId())) {
      roundsReachedMap.put(participant.horseId(), maxRounds - depth);
    }
    encodeStandingsTreeIntoMap(roundsReachedMap, standingsTree.branches()[0], maxRounds, depth + 1);
    encodeStandingsTreeIntoMap(roundsReachedMap, standingsTree.branches()[1], maxRounds, depth + 1);
  }

  /**
   * Build the standings tree from the list of participants
   *
   * @param participantDetailDtoList a list of participants,
   * @param maxRounds                the maximum number of rounds in a tournament where the first draft is round 0.
   * @return the tournament standings tree.
   */
  private TournamentStandingsTreeDto buildStandingsTree(List<TournamentParticipantDetailDto> participantDetailDtoList, int maxRounds) {
    List<TournamentStandingsTreeDto> leafs = participantDetailDtoList.stream()
        .map(participant -> new TournamentStandingsTreeDto(null, participant))
        .sorted(Comparator.comparingInt(leaf -> leaf.thisParticipant().entryNumber()))
        .toList();
    return buildTreeRecursively(leafs, maxRounds, 0);
  }

  private TournamentStandingsTreeDto buildTreeRecursively(List<TournamentStandingsTreeDto> leafs, int maxRounds, int depth) {
    if (maxRounds == depth) {
      return leafs.getFirst();
    } else {
      TournamentStandingsTreeDto upper = buildTreeRecursively(leafs.subList(0, leafs.size() / 2), maxRounds, depth + 1);
      TournamentStandingsTreeDto lower = buildTreeRecursively(leafs.subList(leafs.size() / 2, leafs.size()), maxRounds, depth + 1);
      return new TournamentStandingsTreeDto(
          new TournamentStandingsTreeDto[] {upper, lower},
          upper.thisParticipant().roundReached() > lower.thisParticipant().roundReached() ? upper.thisParticipant() : lower.thisParticipant()
      );
    }
  }

}
