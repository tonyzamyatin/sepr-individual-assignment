package at.ac.tuwien.sepr.assignment.individual.rest;


import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import java.util.stream.Collectors;

import static at.ac.tuwien.sepr.assignment.individual.TestUtil.generateValidTournamentDetailDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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
  ObjectMapper objectMapper;
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
    var validTournamentDto = generateValidTournamentDetailDto();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());  // needed to serialize LocalDate
    String jsonTournamentDto = objectMapper.writeValueAsString(validTournamentDto);

    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .post("/tournaments/create")
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
        () -> assertThat("The participants list should contain the same elements, no matter the order",
            tournamentResult.participants().stream().map(HorseDetailDto::id).collect(Collectors.toList()),
            containsInAnyOrder(validTournamentDto.participants().stream().map(HorseDetailDto::id).toArray(Long[]::new)))
    );
  }
}
