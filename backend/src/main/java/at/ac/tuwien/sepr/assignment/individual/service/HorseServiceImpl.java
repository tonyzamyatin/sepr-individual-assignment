package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final BreedService breedService;

  public HorseServiceImpl(HorseDao dao, HorseMapper mapper, HorseValidator validator, BreedService breedService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.breedService = breedService;
  }

  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) {
    var horses = dao.search(searchParameters);
    // First get all breed ids…
    var breeds = horses.stream()
        .map(Horse::getBreedId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    // … then get the breeds all at once.
    var breedsPerId = breedMapForHorses(breeds);

    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, breedsPerId));
  }


  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException {
    LOG.trace("update({})", horse);
    validator.validateForUpdate(horse);
    var updatedHorse = dao.update(horse);
    var breeds = breedMapForSingleHorse(updatedHorse);
    return mapper.entityToDetailDto(updatedHorse, breeds);
  }


  @Override
  public HorseDetailDto create(HorseDetailDto horse) throws ValidationException {
    LOG.trace("create({})", horse);
    validator.validateForCreate(horse);
    var createdHorse = dao.create(horse);
    var breeds = breedMapForSingleHorse(createdHorse);
    return mapper.entityToDetailDto(createdHorse, breeds);
  }

  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    var breeds = breedMapForSingleHorse(horse);
    return mapper.entityToDetailDto(horse, breeds);
  }


  private Map<Long, BreedDto> breedMapForSingleHorse(Horse horse) {
    if (horse.getBreedId() == null) {
      return Collections.emptyMap();
    }
    return breedMapForHorses(Collections.singleton(horse.getBreedId()));
  }


  private Map<Long, BreedDto> breedMapForHorses(Set<Long> horseIds) {
    // Filter out null values from the set
    Set<Long> nonNullHorseIds = horseIds.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    if (nonNullHorseIds.isEmpty()) {
      return Collections.emptyMap();
    }

    return breedService.findBreedsByIds(nonNullHorseIds)
        .collect(Collectors.toUnmodifiableMap(BreedDto::id, Function.identity()));
  }
}
