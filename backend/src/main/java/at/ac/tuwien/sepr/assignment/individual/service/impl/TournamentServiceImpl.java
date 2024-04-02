package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.StandingsDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.StandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.TournamentMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.TournamentDao;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentParticipantService;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import at.ac.tuwien.sepr.assignment.individual.service.validator.TournamentValidator;
import org.jetbrains.annotations.NotNull;
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
import java.util.stream.IntStream;
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
    List<ParticipantDetailDto> participants = getTournamentParticipantDetailDtos(id);
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
    // Create tournament with sorted participants
    List<ParticipantDetailDto> sortedParticipants = tournament.participants().stream()
        .sorted(Comparator.comparingInt(ParticipantDetailDto::entryNumber))
        .toList();
    Tournament createdTournament = tournamentDao.create(tournament.withParticipants(sortedParticipants));
    // Create participants and catch any ConflictExceptions
    List<ParticipantDetailDto> participants = sortedParticipants.stream().map(participantDetailDto -> {
      try {
        return participantService.create(tournament.id(), participantDetailDto);
      } catch (ConflictException e) {
        conflictErrors.addAll(e.errors());
        return null;
      }
    }).toList();

    if (!conflictErrors.isEmpty()) {
      LOG.error("Creation of tournament failed due to data conflicts: {}", conflictErrors);
      throw new ConflictException("Tournament data is in conflict with system data", conflictErrors);
    }
    return tournamentMapper.entityToDetailDto(createdTournament, participants);
  }

  @Override
  public StandingsDetailDto getStandings(long id) throws NotFoundException {
    LOG.trace("getStandings({})", id);
    Tournament tournament = tournamentDao.getById(id);
    List<ParticipantDetailDto> participants = getTournamentParticipantDetailDtos(id);
    return new StandingsDetailDto(tournament.getId(), tournament.getName(), participants, buildStandingsTree(participants, 3));
  }

  @Override
  public StandingsDetailDto updateStandings(StandingsDetailDto tournamentStandings)
      throws ConflictException, ValidationException, NotFoundException {
    LOG.trace("updateStandings({})", tournamentStandings);
    validator.validateForStandingsUpdate(tournamentStandings);
    List<String> conflictErrors = new ArrayList<>();

    // Check if tournament exists (throws NotFoundException)
    getTournament(tournamentStandings.id());

    // Encode the standings tree into a map of the participants' horse IDs and the rounds they reached
    Map<Long, Integer> roundsReachedMap = new HashMap<>();
    List<ParticipantDetailDto> participantsSortedByEntryInTree = new ArrayList<>();
    encodeStandingsTreeIntoMap(tournamentStandings.tree(), roundsReachedMap, participantsSortedByEntryInTree, 3, 0);
    // Update participant data
    List<ParticipantDetailDto> updatedParticipants = IntStream.range(0, participantsSortedByEntryInTree.size())
        .mapToObj(i -> {
          var participant = participantsSortedByEntryInTree.get(i);
          var updatedParticipant = updateRoundReachedOfParticipant(
              tournamentStandings.id(),
              participant
                  .withRoundReached(roundsReachedMap.get(participant.horseId()))
                  .withEntryNumber(i + 1)
          );
          if (updatedParticipant == null) {
            conflictErrors.add("Tournament standings in conflict with system data");
            return null;
          } else {
            return updatedParticipant;
          }
        })
        .toList();

    if (!conflictErrors.isEmpty()) {
      LOG.error("Update of tournament standings failed due to conflicting data: {}", conflictErrors);
      throw new ConflictException("Tournament standings data for update in conflict with system data", conflictErrors);
    }
    LOG.debug("Participants from DTO: {}", tournamentStandings.participants());
    LOG.debug("Updated participants: {}", updatedParticipants);
    List<ParticipantDetailDto> sortedParticipants = updatedParticipants.stream()
        .sorted(Comparator.comparingInt(ParticipantDetailDto::entryNumber))
        .toList();
    LOG.debug("Updated participants sorted by entry number: {}", sortedParticipants);
    return new StandingsDetailDto(tournamentStandings.id(), tournamentStandings.name(), sortedParticipants, buildStandingsTree(sortedParticipants, 3));
  }

  @Override
  public StandingsDetailDto generateFirstRound(long id) throws NotFoundException {
    LOG.trace("generateFirstRound({})", id);
    Tournament tournament = tournamentDao.getById(id);
    List<ParticipantDetailDto> participants = getTournamentParticipantDetailDtos(id);
    List<ParticipantDetailDto> participantsSortedByPoints = participants.stream()
        .map(participant -> {
          var searchParams = new ParticipantSearchDto(
              participant.horseId(),
              null,
              tournament.getStartDate().minusMonths(12),
              tournament.getStartDate().minusDays(1));
          LOG.debug("Searching for past participations of participant {} with search parameters {}", participant.name(), searchParams);
          List<ParticipantDetailDto> pastParticipations = participantService.searchParticipants(searchParams);
          LOG.debug("Found {} past participations", pastParticipations.size());
          return Map.entry(
              participant,
              pastParticipations.stream()
                  .mapToInt(pastParticipation -> {
                    if (pastParticipation.roundReached() == 3) {
                      return 5;
                    } else if (pastParticipation.roundReached() == 2) {
                      return 3;
                    } else if (pastParticipation.roundReached() == 1) {
                      return 1;
                    } else {
                      return 0;
                    }
                  })
                  .sum()
          );
        })
        .peek(entry -> LOG.debug("Participant {} has {} points", entry.getKey().name(), entry.getValue()))
        .sorted(Comparator.comparingInt(Map.Entry<ParticipantDetailDto, Integer>::getValue).reversed()
            .thenComparing(e -> e.getKey().name())) // Sort by points descending, then by name ascending in case of a tie
        .map(Map.Entry::getKey) // Map each entry to its key, which is the participant
        .toList();
    LOG.debug("Participants sorted by points: {}", participantsSortedByPoints);
    List<ParticipantDetailDto> crossTableSortedParticipants = new ArrayList<>();
    for (int i = 0; i < participantsSortedByPoints.size() / 2; i++) {
      crossTableSortedParticipants.add(participantsSortedByPoints.get(i));
      crossTableSortedParticipants.add(participantsSortedByPoints.get(participantsSortedByPoints.size() - 1 - i));
    }

    List<ParticipantDetailDto> updatedParticipants = IntStream.range(0, participants.size())
        .mapToObj(i -> {
          return crossTableSortedParticipants.get(i)
              .withEntryNumber(i + 1)
              .withRoundReached(0);
        })
        .toList();
    LOG.debug("Participants sorted cross-table-wise: {}", crossTableSortedParticipants);
    return new StandingsDetailDto(
        tournament.getId(),
        tournament.getName(),
        updatedParticipants,
        buildStandingsTree(updatedParticipants, 3));
  }

  @NotNull
  private List<ParticipantDetailDto> getTournamentParticipantDetailDtos(long id) {
    List<ParticipantDetailDto> sortedParticipants = participantService.findParticipantsByTournamentId(id)
        .stream().sorted(Comparator.comparingInt(ParticipantDetailDto::entryNumber)).toList();
    if (sortedParticipants.isEmpty()) { // should never happen
      String errorMessage = "Existing tournament does not have any participants";
      LOG.error("Unexpected error error during retrieval of tournament standings: {}", errorMessage);
      throw new FatalException(errorMessage);
    }
    return sortedParticipants;
  }

  /**
   * Encode the standings tree into a map of the participants' horse IDs and the rounds they reached. This method works recursively in a top-down approach.
   *
   * @param standingsTree    the standings tree to encode
   * @param roundsReachedMap the map to encode the round reached of the participants in the standings tree into
   * @param participants     the list to save the participants in the standings tree into (in the same entry order as in the tree)
   * @param maxRounds        the maximum number of rounds in a tournament where the first draft is round 0
   * @param depth            the current depth of the tree
   */
  private void encodeStandingsTreeIntoMap(StandingsTreeDto standingsTree, Map<Long, Integer> roundsReachedMap, List<ParticipantDetailDto> participants,
                                          int maxRounds, int depth) {
    if (maxRounds - depth < 0 || standingsTree.branches() == null || standingsTree.branches().length == 0) {
      // Add the participant of the leaf node to the participant list
      var participant = standingsTree.thisParticipant();
      if (participant != null) {
        participants.add(participant);
        // Add participant of the leaf node to roundsReachedMap with roundReached 0 before returning if it hasn't been already added
        if (!roundsReachedMap.containsKey(participant.horseId())) {
          roundsReachedMap.put(participant.horseId(), maxRounds - depth);
        }
      }
      return;
    }
    // Add participant of the branch to the map
    var participant = standingsTree.thisParticipant();
    if (participant != null && !roundsReachedMap.containsKey(participant.horseId())) {
      roundsReachedMap.put(participant.horseId(), maxRounds - depth);
    }
    // Recursion over the two sub-branches of this branch
    encodeStandingsTreeIntoMap(standingsTree.branches()[0], roundsReachedMap, participants, maxRounds, depth + 1);
    encodeStandingsTreeIntoMap(standingsTree.branches()[1], roundsReachedMap, participants, maxRounds, depth + 1);
  }

  /**
   * Build the standings tree from the list of participants
   *
   * @param participantDetailDtoList a list of participants,
   * @param maxRounds                the maximum number of rounds in a tournament where the first draft is round 0.
   * @return the tournament standings tree.
   */
  private StandingsTreeDto buildStandingsTree(List<ParticipantDetailDto> participantDetailDtoList, int maxRounds) {
    List<StandingsTreeDto> leafs = participantDetailDtoList.stream().map(participant -> new StandingsTreeDto(null, participant)).toList();
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
  private StandingsTreeDto buildTreeRecursively(List<StandingsTreeDto> leafs, int maxRounds, int depth) {
    if (maxRounds == depth) {
      return leafs.getFirst();
    } else {
      StandingsTreeDto upper = buildTreeRecursively(leafs.subList(0, leafs.size() / 2), maxRounds, depth + 1);
      StandingsTreeDto lower = buildTreeRecursively(leafs.subList(leafs.size() / 2, leafs.size()), maxRounds, depth + 1);
      ParticipantDetailDto winner = computeBranchWinner(upper, lower);
      return new StandingsTreeDto(
          new StandingsTreeDto[] {upper, lower},
          winner
      );
    }
  }

  @Nullable
  private ParticipantDetailDto computeBranchWinner(StandingsTreeDto upper, StandingsTreeDto lower) {
    ParticipantDetailDto winner;
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
   * @return the updated participant, or {@code null} if the participant is not found in the persistent data store.
   */
  private ParticipantDetailDto updateRoundReachedOfParticipant(Long tournamentId, ParticipantDetailDto participantDetailDto) {
    try {
      return participantService.update(tournamentId,
          new ParticipantDetailDto(
              participantDetailDto.horseId(),
              participantDetailDto.name(),
              participantDetailDto.dateOfBirth(),
              participantDetailDto.entryNumber(),
              participantDetailDto.roundReached()));
    } catch (NotFoundException e) {
      return null;
    }
  }

}
