package at.ac.tuwien.sepr.assignment.individual.rest;


import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.TestUtility;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.StandingsDetailDto;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.lang.invoke.MethodHandles;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class TournamentEndpointTest extends TestBase {

  @Autowired
  TestUtility testUtil;
  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  TournamentService tournamentService;
  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }

  @Test
  public void createTournamentWithValidDtoShouldReturnCreatedTournament() throws Exception {
    var validTournamentDto = testUtil.generateValidTournamentDetailDto();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());  // needed to serialize LocalDate
    String jsonTournamentDto = objectMapper.writeValueAsString(validTournamentDto);

    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .post("/tournaments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonTournamentDto)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

    var tournamentResult = assertDoesNotThrow(() -> objectMapper.readValue(body, TournamentDetailDto.class));
    assertNotNull(tournamentResult);
    assertAll(
        "Validating created tournament properties",
        () -> assertEquals(validTournamentDto.id(), tournamentResult.id()),
        () -> assertEquals(validTournamentDto.name(), tournamentResult.name()),
        () -> assertEquals(validTournamentDto.startDate(), tournamentResult.startDate()),
        () -> assertEquals(validTournamentDto.endDate(), tournamentResult.endDate()),
        // assert that both lists contain the same elements in the same order
        () -> assertEquals(validTournamentDto.participants(), tournamentResult.participants())
    );
  }

  @Test
  public void updateStandingsValidFullStandingsTreeShouldReturnUpdatedStandings() throws Exception {
    // Generate a valid tournament and create it in the test DB
    var validTournamentInitial = tournamentService.create(testUtil.generateValidTournamentDetailDto());
    // Modify the tournament first participants to have reached the first round
    List<ParticipantDetailDto> modifiedParticipants = new java.util.ArrayList<>(validTournamentInitial.participants().stream().toList());
    modifiedParticipants.set(0, modifiedParticipants.getFirst().withRoundReached(1));
    var modifiedTournament = validTournamentInitial.withParticipants(modifiedParticipants);
    // Generate a valid tournament standings DTO using the modified tournament
    var validTournamentStandingsForUpdate = testUtil.generateValidTournamentStandings(modifiedTournament);
    var validTournamentStandingsForUpdateJSON = objectMapper.writeValueAsString(validTournamentStandingsForUpdate);

    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .put("/tournaments/standings")
            .contentType(MediaType.APPLICATION_JSON)
            .content(validTournamentStandingsForUpdateJSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    var tournamentResult = assertDoesNotThrow(() -> objectMapper.readValue(body, StandingsDetailDto.class));
    assertNotNull(tournamentResult);
    assertThat(tournamentResult).usingRecursiveComparison().isEqualTo(validTournamentStandingsForUpdate);
  }
}
