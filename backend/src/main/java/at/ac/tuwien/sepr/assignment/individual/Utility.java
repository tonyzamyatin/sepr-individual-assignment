package at.ac.tuwien.sepr.assignment.individual;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class Utility {

  private final HorseService horseService;

  public Utility(HorseService horseService) {
    this.horseService = horseService;
  }

  public Map<Long, HorseDetailDto> mapHorseIdsToDetailDtos(Set<Long> horseIds) {

    Set<Long> nonNullHorseIds = horseIds.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    if (nonNullHorseIds.isEmpty()) {
      return Collections.emptyMap();
    }

    return horseService.findHorsesById(nonNullHorseIds)
        .collect(Collectors.toUnmodifiableMap(HorseDetailDto::id, Function.identity()));
  }
}
