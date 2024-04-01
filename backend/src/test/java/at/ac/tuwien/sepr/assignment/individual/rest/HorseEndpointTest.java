package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.service.TournamentParticipantService;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest extends TestBase {

  @Autowired
  ObjectMapper objectMapper;
  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Mock
  private TournamentParticipantService participantService;



  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/asdf123")).andExpect(status().isNotFound());
  }

  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc.perform(MockMvcRequestBuilders.get("/horses").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn().getResponse()
        .getContentAsByteArray();

    List<HorseListDto> horseResult = objectMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult).hasSize(32).extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth)
        .contains(tuple(-1L, "Wendy", Sex.FEMALE, LocalDate.of(2019, 8, 5)), tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10)),
            tuple(-21L, "Bella", Sex.FEMALE, LocalDate.of(2003, 7, 6)), tuple(-2L, "Hugo", Sex.MALE, LocalDate.of(2020, 2, 20)));
  }

  @Test
  public void searchByBreedWelFindsThreeHorses() throws Exception {
    var body = mockMvc.perform(MockMvcRequestBuilders.get("/horses").queryParam("breed", "Wel").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    var horsesIterator = objectMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body);
    assertNotNull(horsesIterator);
    var horses = new ArrayList<HorseListDto>();
    horsesIterator.forEachRemaining(horses::add);
    // We don't have height and weight of the horses here, so no reason to test for them.
    assertThat(horses).extracting("id", "name", "sex", "dateOfBirth", "breed.name").as("ID, Name, Sex, Date of Birth, Breed Name")
        .containsExactlyInAnyOrder(tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10), "Welsh Cob"),
            tuple(-21L, "Bella", Sex.FEMALE, LocalDate.of(2003, 7, 6), "Welsh Cob"), tuple(-2L, "Hugo", Sex.MALE, LocalDate.of(2020, 2, 20), "Welsh Pony"));
  }

  @Test
  public void searchByBirthDateBetween2017And2018ReturnsFourHorses() {
    var body = assertDoesNotThrow(() -> mockMvc.perform(MockMvcRequestBuilders.get("/horses").queryParam("bornEarliest", LocalDate.of(2017, 3, 5).toString())
            .queryParam("bornLatest", LocalDate.of(2018, 10, 10).toString()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn()
        .getResponse().getContentAsByteArray());

    var horsesResult = assertDoesNotThrow(() -> objectMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body));
    assertNotNull(horsesResult);

    var horses = new ArrayList<HorseListDto>();
    horsesResult.forEachRemaining(horses::add);

    assertThat(horses).hasSize(4).extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth, (h) -> h.breed().name())
        .containsExactlyInAnyOrder(tuple(-24L, "Rocky", Sex.MALE, LocalDate.of(2018, 8, 19), "Dartmoor Pony"),
            tuple(-26L, "Daisy", Sex.FEMALE, LocalDate.of(2017, 12, 1), "Hanoverian"), tuple(-31L, "Leo", Sex.MALE, LocalDate.of(2017, 3, 5), "Haflinger"),
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10), "Welsh Cob"));
  }

  @Test
  public void createHorseWithValidHorseDtoShouldReturnCreatedHorse() throws JsonProcessingException {
    var validHorseDto = new HorseDetailDto(-33L, "Anton", Sex.MALE, LocalDate.of(2004, 3, 24), 1.74f, 68.5f, new BreedDto(-11L, "Lipizzaner"));

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());  // needed to serialize LocalDate in dateOfBirth field of HorseDetailDto
    String jsonHorseDto = objectMapper.writeValueAsString(validHorseDto);

    var body = assertDoesNotThrow(() -> mockMvc.perform(
            MockMvcRequestBuilders.post("/horses").contentType(MediaType.APPLICATION_JSON).content(jsonHorseDto).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString());

    var horseResult = assertDoesNotThrow(() -> objectMapper.readValue(body, HorseDetailDto.class));
    assertNotNull(horseResult);
    assertAll("Validating created horse properties", () -> assertEquals(validHorseDto.id(), horseResult.id(), "Horse ID does not match"),
        () -> assertEquals(validHorseDto.name(), horseResult.name(), "Horse name does not match"),
        () -> assertEquals(validHorseDto.sex(), horseResult.sex(), "Horse sex does not match"),
        () -> assertEquals(validHorseDto.dateOfBirth(), horseResult.dateOfBirth(), "Horse date of birth does not match"),
        () -> assertEquals(validHorseDto.height(), horseResult.height(), "Horse height does not match"),
        () -> assertEquals(validHorseDto.weight(), horseResult.weight(), "Horse weight does not match"),
        () -> assertEquals(validHorseDto.breed().id(), horseResult.breed().id(), "Horse breed ID does not match"));
  }


  @Test
  public void deleteExistingHorseNotParticipatingInTournamentShouldReturnOk200() {
    long horseId = -32L;
    // Mock TournamentService method used by validator in Horse.service.delete(id) method
    when(participantService.isHorseParticipantInAnyTournament(horseId)).thenReturn(false);
    assertDoesNotThrow(() -> mockMvc.perform(MockMvcRequestBuilders.delete("/horses/" + horseId)).andExpect(status().isOk()));
  }

  @Test
  public void deleteNonExistingHorseShouldReturnOk200() {
    long horseId = -33L;
    // Mock TournamentService method used by validator in Horse.service.delete(id) method
    when(participantService.isHorseParticipantInAnyTournament(horseId)).thenReturn(false);
    assertDoesNotThrow(() -> mockMvc.perform(MockMvcRequestBuilders.delete("/horses/" + horseId)).andExpect(status().isOk()));
  }


  @Test
  public void deleteExistingHorseParticipatingInTournamentShouldReturnConflict409() {
    long horseId = -1L;
    // Mock TournamentService method used by validator in Horse.service.delete(id) method
    when(participantService.isHorseParticipantInAnyTournament(horseId)).thenReturn(true);
    assertDoesNotThrow(() -> mockMvc.perform(MockMvcRequestBuilders.delete("/horses/" + horseId)).andExpect(status().isConflict()));
  }
}
