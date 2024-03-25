package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.Optional;

@Component
public class ParticipantMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  public TournamentParticipantDetailDto entityToDetailDto(Participant participant, Map<Long, HorseDetailDto> horseMap) {
    LOG.trace("entityToDto({})", participant);
    if (participant == null) {
      return null;
    }
    var horseDetails = horseFromMap(participant, horseMap);
    return new TournamentParticipantDetailDto(
        participant.getHorseId(),
        horseDetails.name(),
        horseDetails.dateOfBirth(),
        participant.getEntryNumber(),
        participant.getRoundReached()
    );
  }

  private HorseDetailDto horseFromMap(Participant participant, Map<Long, HorseDetailDto> map) {
    var horseId = participant.getHorseId();
    if (horseId == null) {   // Horse id part of primary identifier
      throw new FatalException("Participant without horse id");
    } else {
      return Optional.ofNullable(map.get(horseId))
          .orElseThrow(() -> new FatalException(
              "Participant with non-existent horse id " + participant.getHorseId())
          );
    }
  }
}
