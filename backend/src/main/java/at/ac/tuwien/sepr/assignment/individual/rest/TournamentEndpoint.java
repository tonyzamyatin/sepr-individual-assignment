package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = TournamentEndpoint.BASE_PATH)
public class TournamentEndpoint {
  static final String BASE_PATH = "/tournaments";
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final TournamentService service;

  public TournamentEndpoint(TournamentService service) {
    this.service = service;
  }

  @GetMapping
  public Stream<TournamentListDto> searchTournaments(TournamentSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping()
  public TournamentDetailDto create(@RequestBody TournamentDetailDto toCreate) throws ValidationException, ConflictException {
    LOG.info("POST " + BASE_PATH + "/create");
    LOG.debug("Body of request:\n{}", toCreate);
    return service.create(toCreate);
  }

  @GetMapping("standings/{id}")
  public TournamentStandingsDto getStandings(@PathVariable("id") long id) throws NotFoundException {
    LOG.info("GET " + BASE_PATH + "/standings/" + id);
    return service.getStandings(id);
  }

  @PutMapping("standings")
  public TournamentStandingsDto updateStandings(@RequestBody TournamentStandingsDto tournamentStandings)
      throws ConflictException, ValidationException, NotFoundException {
    LOG.info("PUT " + BASE_PATH + "/standings");
    LOG.debug("Body of request:\n{}", tournamentStandings);
    return service.updateStandings(tournamentStandings);
  }
}
