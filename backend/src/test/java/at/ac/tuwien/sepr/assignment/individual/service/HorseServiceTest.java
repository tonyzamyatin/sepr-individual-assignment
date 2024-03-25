package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
public class HorseServiceTest extends TestBase {

  @Autowired
  HorseService horseService;

  @Mock
  private TournamentService tournamentService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  public void searchByBreedWelFindsThreeHorses() {
    var searchDto = new HorseSearchDto(null, null, null, null, "Wel", null);
    var horses = horseService.search(searchDto);
    assertNotNull(horses);
    // We don't have height and weight of the horses here, so no reason to test for them.
    assertThat(horses)
        .extracting("id", "name", "sex", "dateOfBirth", "breed.name")
        .as("ID, Name, Sex, Date of Birth, Breed Name")
        .containsOnly(
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10), "Welsh Cob"),
            tuple(-21L, "Bella", Sex.FEMALE, LocalDate.of(2003, 7, 6), "Welsh Cob"),
            tuple(-2L, "Hugo", Sex.MALE, LocalDate.of(2020, 2, 20), "Welsh Pony")
        );
  }

  @Test
  public void searchByBirthDateBetween2017And2018ReturnsFourHorses() {
    var searchDto = new HorseSearchDto(null, null,
        LocalDate.of(2017, 3, 5),
        LocalDate.of(2018, 10, 10),
        null, null);
    var horses = horseService.search(searchDto);
    assertNotNull(horses);
    assertThat(horses)
        .hasSize(4)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth, (h) -> h.breed().name())
        .containsExactlyInAnyOrder(
            tuple(-24L, "Rocky", Sex.MALE, LocalDate.of(2018, 8, 19),
                "Dartmoor Pony"),
            tuple(-26L, "Daisy", Sex.FEMALE, LocalDate.of(2017, 12, 1),
                "Hanoverian"),
            tuple(-31L, "Leo", Sex.MALE, LocalDate.of(2017, 3, 5),
                "Haflinger"),
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10),
                "Welsh Cob"));
  }

  @Test
  public void createHorseWithValidHorseDtoShouldReturnCreatedHorse() {
    var validHorseDto =
        new HorseDetailDto(
            -33L,
            "Anton",
            Sex.MALE,
            LocalDate.of(2004, 3, 24),
            1.74f,
            68.5f,
            new BreedDto(-11L, "Lipizzaner"));
    var createdHorse = assertDoesNotThrow(() -> horseService.create(validHorseDto));
    assertInstanceOf(HorseDetailDto.class, createdHorse);
    assertAll(
        "insertedHorse",
        () -> assertEquals(-33L, createdHorse.id()),
        () -> assertEquals("Anton", createdHorse.name()),
        () -> assertEquals(Sex.MALE, createdHorse.sex()),
        () -> assertEquals(LocalDate.of(2004, 3, 24), createdHorse.dateOfBirth()),
        () -> assertEquals(1.74f, createdHorse.height()),
        () -> assertEquals(68.5f, createdHorse.weight()),
        () -> assertEquals(-11L, createdHorse.breed().id()));
  }

  @Test
  public void deleteHorseWithExistingId() {
    assertDoesNotThrow(() -> horseService.delete(-32L));
  }

  @Test
  public void deleteHorseWithNonExistentIdShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> horseService.delete(-33L));
  }

  @Test
  public void deleteHorseParticipatingInTournamentShouldThrowConflictException() {
    long horseId = -1L;
    // Mock TournamentService method used by validator in Horse.service.delete(id) method
    when(tournamentService.isHorseParticipantInAnyTournament(horseId)).thenReturn(true);
    assertThrows(ConflictException.class, () -> horseService.delete(horseId));
  }
}
