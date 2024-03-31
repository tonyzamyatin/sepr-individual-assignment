package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


@Service
public class TournamentServiceImpl implements TournamentService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentDao tournamentDao;
  private final TournamentParticipantService participantService;
  private final TournamentMapper tournamentMapper;
  private final TournamentValidator validator;

  public TournamentServiceImpl(TournamentDao tournamentDao, TournamentParticipantService participantService, TournamentMapper tournamentMapper,
                               TournamentValidator validator) {
    this.tournamentDao = tournamentDao;
    this.participantService = participantService;
    this.tournamentMapper = tournamentMapper;
    this.validator = validator;
  }

  @Override
  public TournamentDetailDto getTournament(long id) throws NotFoundException {
    LOG.trace("getTournament({})", id);
    Tournament tournament = tournamentDao.getById(id);
    List<TournamentParticipantDetailDto> participants = participantService.findParticipantsByTournamentId(id);
    if (participants.isEmpty()) { // should never happen
      throw new FatalException("Existing tournament does not have any participants");
    }
    return tournamentMapper.entityToDetailDto(tournament, participants);
  }

  @Override
  public boolean doesTournamentExist(long id) {
    LOG.trace("doesTournamentExist({})", id);
    try {
      tournamentDao.getById(id);
    } catch (NotFoundException e) {
      return false;
    }
    return true;
  }

  @Override
  public Stream<TournamentListDto> search(TournamentSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    var tournaments = tournamentDao.search(searchParameters);
    return tournaments.stream().map(tournamentMapper::entityToListDto);
  }

  @Override
  public TournamentDetailDto create(TournamentDetailDto tournament) throws ValidationException, ConflictException {
    LOG.trace("create({})", tournament);
    validator.validateForCreate(tournament);
    List<String> conflictErrors = new ArrayList<>();

    // Create tournament
    Tournament createdTournament = tournamentDao.create(tournament);

    // Create participants and catch any ConflictExceptions
    List<TournamentParticipantDetailDto> participants = tournament.participants().stream().map(participantDetailDto -> {
      try {
        return participantService.create(tournament.id(), participantDetailDto);
      } catch (ConflictException e) {
        conflictErrors.addAll(e.errors());
        return null;
      }
    }).toList();

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Tournament data is in conflict with system data", conflictErrors);
    }
    return tournamentMapper.entityToDetailDto(createdTournament, participants);
  }

  @Override
  public TournamentStandingsDto getStandings(long id) throws NotFoundException {
    LOG.trace("getStandings({})", id);
    Tournament tournament = tournamentDao.getById(id);
    List<TournamentParticipantDetailDto> participants = participantService.findParticipantsByTournamentId(id);
    if (participants.isEmpty()) { // should never happen
      throw new FatalException("Existing tournament does not have any participants");
    }
    return new TournamentStandingsDto(tournament.getId(), tournament.getName(), participants, buildStandingsTree(participants, 3));
  }

  @Override
  public TournamentStandingsDto updateStandings(TournamentStandingsDto tournamentStandings)
      throws ConflictException, ValidationException, NotFoundException {
    LOG.trace("updateStandings({})", tournamentStandings);
    validator.validateForStandingsUpdate(tournamentStandings);
    List<String> conflictErrors = new ArrayList<>();

    // Check if tournament exists (throws NotFoundException)
    getTournament(tournamentStandings.id());

    // Encode the standings tree into a map of the participants' horse IDs and the rounds they reached
    Map<Long, Integer> roundReachedMap = new HashMap<>();
    encodeStandingsTreeIntoMap(roundReachedMap, tournamentStandings.tree(), 3, 0);

    // Update participant data
    List<TournamentParticipantDetailDto> participants =
        tournamentStandings.participants().stream()
            .map(participantDetailDto -> updateRoundReachedOfParticipant(tournamentStandings.id(), participantDetailDto, roundReachedMap))
            .peek(participant -> {
              if (participant == null) {
                conflictErrors.add("Tournament standings in conflict with system data");
              }
            }).toList();

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Tournament standings in conflict with system data", conflictErrors);
    }
    return new TournamentStandingsDto(tournamentStandings.id(), tournamentStandings.name(), participants, buildStandingsTree(participants, 3));
  }

  /**
   * Encode the standings tree into a map of the participants' horse IDs and the rounds they reached. This method works recursively in a top-down approach.
   *
   * @param roundsReachedMap the map to encode the standings tree into
   * @param standingsTree    the standings tree to encode
   * @param maxRounds        the maximum number of rounds in a tournament where the first draft is round 0
   * @param depth            the current depth of the tree
   */
  private void encodeStandingsTreeIntoMap(Map<Long, Integer> roundsReachedMap, TournamentStandingsTreeDto standingsTree, int maxRounds, int depth) {
    if (maxRounds - depth < 0 || standingsTree.branches() == null || standingsTree.branches().length == 0) {
      // Add the participant of the leaf node to the map before returning
      var participant = standingsTree.thisParticipant();
      if (participant != null && !roundsReachedMap.containsKey(participant.horseId())) {
        roundsReachedMap.put(participant.horseId(), maxRounds - depth);
      }
      return;
    }
    // Add participant of the branch to the map
    var participant = standingsTree.thisParticipant();
    if (participant != null && !roundsReachedMap.containsKey(participant.horseId())) {
      roundsReachedMap.put(participant.horseId(), maxRounds - depth);
    }
    // Recursion over the two sub-branches of this branch
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
    List<TournamentStandingsTreeDto> leafs = participantDetailDtoList.stream().map(participant -> new TournamentStandingsTreeDto(null, participant))
        .sorted(Comparator.comparingInt(leaf -> leaf.thisParticipant().entryNumber())).toList();
    return buildTreeRecursively(leafs, maxRounds, 0);
  }

  /**
   * Build standings tree recursively from the list of leafs.
   *
   * @param leafs     the list of leafs, where each leaf is a TournamentStandingsTreeDto with no branches and a participant.
   * @param maxRounds the maximum number of rounds in a tournament where the first draft is round 0.
   * @param depth     the current depth of the tree.
   * @return the tournament standings tree.
   */
  private TournamentStandingsTreeDto buildTreeRecursively(List<TournamentStandingsTreeDto> leafs, int maxRounds, int depth) {
    if (maxRounds == depth) {
      return leafs.getFirst();
    } else {
      TournamentStandingsTreeDto upper = buildTreeRecursively(leafs.subList(0, leafs.size() / 2), maxRounds, depth + 1);
      TournamentStandingsTreeDto lower = buildTreeRecursively(leafs.subList(leafs.size() / 2, leafs.size()), maxRounds, depth + 1);
      TournamentParticipantDetailDto winner = computeBranchWinner(upper, lower);
      return new TournamentStandingsTreeDto(
          new TournamentStandingsTreeDto[] {upper, lower},
          winner
      );
    }
  }

  @Nullable
  private TournamentParticipantDetailDto computeBranchWinner(TournamentStandingsTreeDto upper, TournamentStandingsTreeDto lower) {
    TournamentParticipantDetailDto winner;
    if (upper.thisParticipant() != null && lower.thisParticipant() != null) {
      int comparisonResult = upper.thisParticipant().compareTo(lower.thisParticipant());
      if (comparisonResult > 0) {
        winner = upper.thisParticipant();
      } else if (comparisonResult < 0) {
        winner = lower.thisParticipant();
      } else {
        // upper.thisParticipant().compareTo(lower.thisParticipant()) == 0
        winner = null;
      }
    } else {
      // both upper.thisParticipant() and lower.thisParticipant() are null
      winner = null;
    }
    return winner;
  }

  /**
   * Updates the reached round of a single tournament participant.
   *
   * @param tournamentId         the tournament ID of tournament that the participant participates in
   * @param participantDetailDto the participant to update
   * @param roundReachedMap      a map of participant's horse IDs mapping to the round they reached in the tournament with the given ID
   * @return the updated participant, or {@code null} if the participant is not found in the persistent data store.
   */
  private TournamentParticipantDetailDto updateRoundReachedOfParticipant(Long tournamentId, TournamentParticipantDetailDto participantDetailDto,
                                                                         Map<Long, Integer> roundReachedMap) {
    try {
      return participantService.update(tournamentId,
          new TournamentParticipantDetailDto(
              participantDetailDto.horseId(),
              participantDetailDto.name(),
              participantDetailDto.dateOfBirth(),
              participantDetailDto.entryNumber(),
              roundReachedMap.get(participantDetailDto.horseId())));
    } catch (NotFoundException e) {
      return null;
    }
  }

}
