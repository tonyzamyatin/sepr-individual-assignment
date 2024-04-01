package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.List;

@Component
public class TournamentMapper {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

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
   * @param participants a list of the tournaments participants
   * @return the converted {@link TournamentDetailDto}
   */
  public TournamentDetailDto entityToDetailDto(Tournament tournament, List<TournamentParticipantDetailDto> participants) {
    LOG.trace("entityToDto({})", tournament);
    if (tournament == null) {
      return null;
    }
    List<TournamentParticipantDetailDto> sortedParticipants = participants.stream()
        .sorted(Comparator.comparingInt(TournamentParticipantDetailDto::entryNumber))
        .toList();

    return new TournamentDetailDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        tournament.getEndDate(),
        sortedParticipants
    );
  }
}
