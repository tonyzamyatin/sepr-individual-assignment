package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.service.BreedService;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.validator.HorseValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    LOG.trace("search({})", searchParameters);
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
  public Stream<HorseDetailDto> findHorsesById(Set<Long> horseIds) {
    LOG.trace("findHorsesById({})", horseIds);
    return dao.findHorsesById(horseIds)
        .stream()
        .map(horse -> mapper.entityToDetailDto(horse, breedMapForSingleHorse(horse)));
  }

  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    var breeds = breedMapForSingleHorse(horse);
    return mapper.entityToDetailDto(horse, breeds);
  }

  @Override
  public HorseDetailDto create(HorseDetailDto horse) throws ValidationException, ConflictException {
    LOG.trace("create({})", horse);

    // Validate provided data for required fields, correct formats and correct ranges
    validator.validateForCreate(horse);

    // Check whether provided data is consistent with system data
    checkForDataConsistency(horse);

    var createdHorse = dao.create(horse);
    var breeds = breedMapForSingleHorse(createdHorse);
    return mapper.entityToDetailDto(createdHorse, breeds);
  }

  @Override
  public HorseDetailDto update(HorseDetailDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);

    // Validate provided data for required fields, correct formats and correct ranges
    validator.validateForUpdate(horse);

    // Check whether provided data is consistent with system data
    checkForDataConsistency(horse);

    var updatedHorse = dao.update(horse);
    var breeds = breedMapForSingleHorse(updatedHorse);
    return mapper.entityToDetailDto(updatedHorse, breeds);
  }

  @Override
  public void delete(long id) throws NotFoundException, ConflictException {
    LOG.trace("delete({})", id);
    validator.validateForDelete(id);
    dao.delete(id);
  }

  private void checkForDataConsistency(HorseDetailDto horse) throws ConflictException {
    List<String> conflictErrors = new ArrayList<>();
    if (horse.breed() != null) {
      List<BreedDto> foundBreeds = breedService.findBreedsByIds(Collections.singleton(horse.breed().id())).toList();
      if (foundBreeds.isEmpty()) {
        conflictErrors.add("Breed with id %d does not exist".formatted(horse.breed().id()));
      }
    }
    if (!conflictErrors.isEmpty()) {
      LOG.error("Update of horse failed due to conflicting data: {}", conflictErrors);
      throw new ConflictException("Horse data for update is in conflict with system data", conflictErrors);
    }
  }

  private Map<Long, BreedDto> breedMapForSingleHorse(Horse horse) {
    if (horse.getBreedId() == null) {
      return Collections.emptyMap();
    }
    return breedMapForHorses(Collections.singleton(horse.getBreedId()));
  }

  private Map<Long, BreedDto> breedMapForHorses(Set<Long> breedIds) {
    // Filter out null values from the set
    Set<Long> nonNullBreedIds = breedIds.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    if (nonNullBreedIds.isEmpty()) {
      return Collections.emptyMap();
    }

    return breedService.findBreedsByIds(nonNullBreedIds)
        .collect(Collectors.toUnmodifiableMap(BreedDto::id, Function.identity()));
  }
}
