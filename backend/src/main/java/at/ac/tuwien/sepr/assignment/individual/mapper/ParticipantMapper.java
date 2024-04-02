package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Optional;

@Component
public class ParticipantMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final HorseService horseService;

  public ParticipantMapper(HorseService horseService) {
    this.horseService = horseService;
  }


  /**
   * Converts a participant entity to a {@link ParticipantDetailDto}.
   *
   * @param participant the participant to convert
   * @return the converted {@link ParticipantDetailDto}
   */
  public ParticipantDetailDto entityToDetailDto(Participant participant) {
    LOG.trace("entityToDto({})", participant);
    if (participant == null) {
      return null;
    }
    var horseDetails = mapParticipantToHorseDetail(participant);
    return new ParticipantDetailDto(
        participant.getHorseId(),
        horseDetails.name(),
        horseDetails.dateOfBirth(),
        participant.getEntryNumber(),
        participant.getRoundReached()
    );
  }

  private HorseDetailDto mapParticipantToHorseDetail(Participant participant) {
    var horseId = participant.getHorseId();
    if (horseId == null) {   // Horse id part of primary identifier
      throw new FatalException("Participant without horse id");
    } else {
      return Optional.ofNullable(retrieveHorseDetail(horseId))
          .orElseThrow(() -> new FatalException(
              "Participant with non-existent horse id " + participant.getHorseId())
          );
    }
  }

  private HorseDetailDto retrieveHorseDetail(long id) {
    try {
      return horseService.getById(id);
    } catch (NotFoundException e) {
      return null;
    }
  }
}
