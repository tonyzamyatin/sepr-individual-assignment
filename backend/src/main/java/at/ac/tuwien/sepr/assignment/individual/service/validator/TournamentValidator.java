package at.ac.tuwien.sepr.assignment.individual.service.validator;

import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.StandingsDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.StandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public void validateForCreate(TournamentDetailDto tournament) throws ValidationException {
    LOG.trace("validateForCreate({})", tournament);
    List<String> validationErrors = new ArrayList<>();

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
    } else {
      if (tournament.participants().size() != 8 || tournament.participants().contains(null)) {
        validationErrors.add("Given participant list contains does not contain exactly 8 participants.");
      } else {
        for (ParticipantDetailDto participant : tournament.participants()) {
          if (participant.entryNumber() == null) {
            validationErrors.add("Entry number missing");
          } else if (participant.entryNumber() < 1 || participant.entryNumber() > 8) {
            validationErrors.add("Entry number must be between 1 and 8");
          }
          if (participant.roundReached() == null) {
            validationErrors.add("Entry number missing");
          } else if (participant.roundReached() < 0 || participant.roundReached() > 3) {
            validationErrors.add("Round reached must be between 0 and 3");
          }
        }
      }
    }

    if (!validationErrors.isEmpty()) {
      LOG.warn("Validation of tournament for create failed: {}", validationErrors);
      throw new ValidationException("Validation of tournament for create failed", validationErrors);
    }
  }

  public void validateForStandingsUpdate(StandingsDetailDto tournamentStandings) throws ValidationException {
    LOG.trace("validateForStandingsUpdate({})", tournamentStandings);
    List<String> validationErrors = new ArrayList<>();
    if (tournamentStandings.name() == null) {
      validationErrors.add("No name given");
    } else if (tournamentStandings.name().isEmpty()) {
      validationErrors.add("Given name is empty");
    } else if (tournamentStandings.name().length() > 100) {
      validationErrors.add("Given name is longer than 100 characters");
    }
    if (tournamentStandings.participants() == null) {
      validationErrors.add("No participant list given");
    } else if (tournamentStandings.participants().size() != 8 || tournamentStandings.participants().contains(null)) {
      validationErrors.add("Given participant list does not contain exactly 8 participants.");
    }
    if (tournamentStandings.tree() == null) {
      validationErrors.add("No standings tree given");
    } else {
      // Validate that all branches in the standings tree split into exactly two subtrees and that the whole tree
      // has 8 leaf nodes with thisParticipant != null and branches == null.
      StandingsTreeDto standingsTree = tournamentStandings.tree();
      Stack<StandingsTreeDto> branches = new Stack<>();
      branches.add(standingsTree);
      int leafNodesCount = 0;
      while (!branches.isEmpty()) {
        StandingsTreeDto branch = branches.pop();
        if (branch != null) {
          if (branch.branches() == null || branch.branches().length == 0) {
            leafNodesCount++;
            if (branch.thisParticipant() == null) {
              validationErrors.add("Leaf node without participant found.");
            }
          } else if (branch.branches().length != 2) {
            validationErrors.add("Every branch of the standings tree (with the exception of the leaf nodes) must split exactly into two subtrees.");
            break;
          } else {
            branches.add(branch.branches()[0]);
            branches.add(branch.branches()[1]);
          }
        }
      }
      if (leafNodesCount != 8) {
        validationErrors.add("Standings tree should end in exactly 8 leaf nodes.");
      }
    }

    if (!validationErrors.isEmpty()) {
      LOG.warn("Validation of tournament for create failed: {}", validationErrors);
      throw new ValidationException("Validation of data for tournament standings update failed", validationErrors);
    }
  }
}
