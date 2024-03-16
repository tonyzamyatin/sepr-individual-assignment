package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import java.util.Map;
import java.util.List;
import java.util.Optional;

@Component
public class TournamentMapper {

  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public TournamentListDto entitiyToListDto(Tournament tournament) {
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
  public TournamentDetailDto entityToDetailDto(Tournament tournament, Map<Long, HorseDetailDto> horses) {
    LOG.trace("entityToDto({})", tournament);
    if (tournament == null) {
      return null;
    }
    return new TournamentDetailDto(
        tournament.getId(),
        tournament.getName(),
        tournament.getStartDate(),
        tournament.getEndDate(),
        horseListFromMap(tournament, horses)
    );
  }

  private List<HorseDetailDto> horseListFromMap(Tournament tournament, Map<Long, HorseDetailDto> map) {
    return Arrays.stream(tournament.getParticipantIds())
        .map(id -> Optional.ofNullable(map.get(id))
            .orElseThrow(() -> new FatalException(
                "Saved tournament " + tournament.getId() + " refers to non-existing horse with id " + id)))
        .toList();
  }
}
