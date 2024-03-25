package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final HorseService horseService;
  private final TournamentService tournamentService;

  public TournamentValidator(@Lazy HorseService horseService, @Lazy TournamentService tournamentService) {
    this.horseService = horseService;
    this.tournamentService = tournamentService;
  }

  public void validateForCreate(TournamentDetailDto tournament) throws ValidationException, ConflictException {
    LOG.trace("validateForCreate({})", tournament);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    // Ignore whether horse has an ID or not (compromise for testing)
    if (tournament.name() == null || tournament.name().isEmpty()) {
      validationErrors.add("No name given");
    } else if (tournament.name().length() > 100) {
      validationErrors.add("Given name is longer than 100 characters");
    }
    if (tournament.startDate().isAfter(tournament.endDate())) {
      validationErrors.add("Given end data is before start date");
    }
    if (tournament.participants() == null) {
      validationErrors.add("No participant list given");
    } else if (tournament.participants().size() != 8 || tournament.participants().contains(null)) {
      validationErrors.add("Given participant list contains does not contain exactly 8 participants.");
    } else {
      // Simplified check whether participants exist by checking whether horses with the corresponding id can be found
      tournament.participants()
          .forEach(participantDetailDto -> {
            // TODO: Validate entryNumber and roundReached of participants
            try {
              var horse = horseService.getById((participantDetailDto.horseId()));
              if (!horse.dateOfBirth().equals(participantDetailDto.dateOfBirth())) {
                conflictErrors.add(
                    "Participant's date of birth with horse id " + participantDetailDto.horseId() + " does not match the date of birth of horse; actual: "
                        + participantDetailDto.dateOfBirth() + " , expected: " + horse.dateOfBirth());
              }
              if (!horse.name().equals(participantDetailDto.name())) {
                conflictErrors.add("Participant's name with horse id " + participantDetailDto.horseId() + " does not match the name of horse; actual: "
                    + participantDetailDto.name() + " , expected: " + horse.name());
              }
            } catch (NotFoundException e) {
              conflictErrors.add("Participant horse with id %d does not exist".formatted(participantDetailDto.horseId()));
            }
          });
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }
    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Tournament data is in conflict with system data", conflictErrors);
    }
  }

  public void validateForStandingsUpdate(long id, TournamentStandingsDto tournamentStandings) throws ValidationException, ConflictException {
    LOG.trace("validateForStandingsUpdate({})", tournamentStandings);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();
    if (id != tournamentStandings.id()) {
      validationErrors.add(
          "Tournament ID of requested resource standings/" + id + " and tournament id " + tournamentStandings.id() + " from DTO do not match.");
    }
    try {
      tournamentService.getById(id);
    } catch (NotFoundException e) {
      conflictErrors.add("Tournament with ID %d does not exist".formatted(id));
    }
    // Validate that all branches in the standings tree split into exactly two subtrees.
    TournamentStandingsTreeDto standingsTree = tournamentStandings.tree();
    Stack<TournamentStandingsTreeDto> branches = new Stack<>();
    branches.add(standingsTree);
    while (!branches.isEmpty()) {
      TournamentStandingsTreeDto branch = branches.pop();
      if (branch.branches().length != 2) {
        validationErrors.add("Every branch of the standings tree (with the exception of the leaf nodes) must split exactly into two subtrees.");
        break;
      } else {
        branches.add(branch.branches()[0]);
        branches.add(branch.branches()[1]);
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of data for tournament standings update failed", validationErrors);
    }
    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Tournament standings data is in conflict with system data", conflictErrors);
    }
  }
}
