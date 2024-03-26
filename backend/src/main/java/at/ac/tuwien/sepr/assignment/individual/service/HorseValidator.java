package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final BreedService breedService;
  private final TournamentParticipantService participantService;

  public HorseValidator(@Lazy BreedService breedService, @Lazy TournamentParticipantService participantService) {
    this.breedService = breedService;
    this.participantService = participantService;
  }

  public void validateForUpdate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }
    validateNonIdHorseData(horse, validationErrors);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of provided horse data failed", validationErrors);
    }
  }

  public void validateForCreate(HorseDetailDto horse) throws ValidationException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();
    List<String> conflictErrors = new ArrayList<>();

    validateNonIdHorseData(horse, validationErrors);

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of provided horse data failed", validationErrors);
    }
  }

  public void validateForDelete(long id) throws ConflictException {
    LOG.trace("validateForDelete({})", id);
    List<String> conflictErrors = new ArrayList<>();
    if (participantService.isHorseParticipantInAnyTournament(id)) {
      conflictErrors.add("Horse with participates in a tournament");
    }

    if (!conflictErrors.isEmpty()) {
      throw new ConflictException("Deletion of horse with id " + id + " is in conflict with system data", conflictErrors);
    }
  }


  private void validateNonIdHorseData(HorseDetailDto horse, List<String> validationErrors) {
    if (horse.name() == null || horse.name().isEmpty()) {
      validationErrors.add("No name given");
    } else if (horse.name().length() > 100) {
      validationErrors.add("Horse name too long. It must be no longer than 100 characters");
    }
    if (horse.sex() == null) {
      validationErrors.add("No sex given");
    } else if (!horse.sex().equals(Sex.MALE) && !horse.sex().equals(Sex.FEMALE)) {
      validationErrors.add("Sex must be either male or female");
    }
    if (horse.dateOfBirth() == null) {
      validationErrors.add("No date of birth given");
    }
    if (horse.height() <= 0f || horse.height() > 9.99f) {
      validationErrors.add("Horse height must be between 0 and 10");
    }
    if (horse.weight() <= 0f || horse.weight() > 999.99f) {
      validationErrors.add("Horse weight must be between 0 and 1000");
    }
  }
}
