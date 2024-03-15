package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Component
public class TournamentValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final HorseService horseService;

  public TournamentValidator(HorseService horseService) {
    this.horseService = horseService;
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
          .forEach(horseDetailDto -> {
            try {
              horseService.getById((horseDetailDto.id()));
            } catch (NotFoundException e) {
              conflictErrors.add("Participant horse with id %d does not exist".formatted(horseDetailDto.id()));
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
}
