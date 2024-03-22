package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  static final String BASE_PATH = "/horses";
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseService service;

  public HorseEndpoint(HorseService service) {
    this.service = service;
  }


  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }


  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable("id") long id) throws NotFoundException {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    return service.getById(id);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("create")
  public HorseDetailDto create(@RequestBody HorseDetailDto toCreate) throws ValidationException, ConflictException {
    LOG.info("POST " + BASE_PATH + "/create");
    LOG.debug("Body of request:\n{}", toCreate);
    return service.create(toCreate);
  }


  @PutMapping("update/{id}")
  public HorseDetailDto update(@PathVariable("id") long id, @RequestBody HorseDetailDto toUpdate)
      throws ValidationException, ConflictException, NotFoundException {
    LOG.info("PUT " + BASE_PATH + "/update/{}", id);
    LOG.debug("Body of request:\n{}", toUpdate);
    return service.update(toUpdate.withId(id));
  }

  @DeleteMapping("delete/{id}")
  public void delete(@PathVariable("id") long id) throws ConflictException, NotFoundException {
    LOG.info("DELETE " + BASE_PATH + "/delete/{}", id);
    service.delete(id);
  }


  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }
}
