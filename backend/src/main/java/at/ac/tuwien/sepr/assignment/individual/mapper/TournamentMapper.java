package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Participant;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TournamentMapper {

  private final ParticipantMapper participantMapper;

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public TournamentMapper(ParticipantMapper participantMapper) {
    this.participantMapper = participantMapper;
  }

  public TournamentListDto entityToListDto(Tournament tournament) {
    LOG.trace("entityToListDto({})", tournament);
    if (tournament == null) {
      return null;
    }

    return new TournamentListDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        tournament.getEndDate()
    );
  }

  /**
   * Convert a tournament entity object to a {@link TournamentDetailDto}.
   * The given map of horses needs to map the ids of the participant horses to their respective {@link HorseDetailDto}.
   *
   * @param tournament the tournament to convert
   * @return the converted {@link TournamentDetailDto}
   */
  public TournamentDetailDto entityToDetailDto(Tournament tournament, List<Participant> participants, Map<Long, HorseDetailDto> horseMap) {
    LOG.trace("entityToDto({})", tournament);
    if (tournament == null) {
      return null;
    }
    List<TournamentParticipantDetailDto> sortedParticipants = participants.stream()
        .map(participant -> participantMapper.entityToDetailDto(participant, horseMap))
        .sorted(Comparator.comparingInt(TournamentParticipantDetailDto::entryNumber))
        .collect(Collectors.toList());

    return new TournamentDetailDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        tournament.getEndDate(),
        sortedParticipants
    );
  }
}
