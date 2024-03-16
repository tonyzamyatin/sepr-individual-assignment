package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static at.ac.tuwien.sepr.assignment.individual.TestUtil.generateValidTournamentDetailDto;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles({"test", "datagen"})
@SpringBootTest
public class TournamentServiceTest extends TestBase {

  @Autowired
  TournamentService tournamentService;

  @Test
  public void createdTournamentWithValidDtoShouldReturnCreatedTournament() {
    var validTournamentDto = generateValidTournamentDetailDto();

    var createdTournament = assertDoesNotThrow(() -> tournamentService.create(validTournamentDto));
    assertNotNull(createdTournament);
    assertInstanceOf(TournamentDetailDto.class, createdTournament);
    assertAll(
        "createdTournament",
        () -> assertEquals(validTournamentDto.id(), createdTournament.id()),
        () -> assertEquals(validTournamentDto.name(), createdTournament.name()),
        () -> assertEquals(validTournamentDto.startDate(), createdTournament.startDate()),
        () -> assertEquals(validTournamentDto.endDate(), createdTournament.endDate()),
        () -> assertArrayEquals(validTournamentDto.participants().stream().map(HorseDetailDto::id).toArray(Long[]::new),
            createdTournament.participants().stream().map(HorseDetailDto::id).toArray(Long[]::new))
    );
  }

  @Test
  public void createTournamentWithNullAsNameShouldThrowValidationException() {
    var validTournamentDto = generateValidTournamentDetailDto();
    var tournamentWithNameNull = validTournamentDto.withName(null);
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNameNull));
  }

  @Test
  public void createTournamentWithEmptyStringAsNameShouldThrowValidationException() {
    var validTournamentDto = generateValidTournamentDetailDto();
    var tournamentWithNameNull = validTournamentDto.withName("");
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNameNull));
  }

  @Test
  public void createTournamentWithSevenHorsesShouldThroughValidationException() {
    var tournamentWithSevenHorses = generateValidTournamentDetailDto();
    tournamentWithSevenHorses.participants().removeLast();
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithSevenHorses));
  }

  @Test
  public void createTournamentWithNineHorsesShouldThroughValidationException() {
    var tournamentWithNineHorses = generateValidTournamentDetailDto();
    tournamentWithNineHorses.participants().add(new HorseDetailDto(
        null,
        null,
        null,
        null,
        0f,
        0,
        null
    ));
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNineHorses));
  }

  @Test
  public void createTournamentWhereOneHorseIsNullShouldThroughValidationException() {
    var tournamentWithNullHorses = generateValidTournamentDetailDto();
    tournamentWithNullHorses.participants().removeLast();
    tournamentWithNullHorses.participants().add(null);
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNullHorses));
  }

  @Test
  public void createTournamentWhereParticipantsListIsNullShouldThroughValidationException() {
    var tournamentWithNullParticipants = generateValidTournamentDetailDto().withParticipants(null);
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNullParticipants));
  }

  @Test
  public void createTournamentWhereOneHorseIsNotInDbShouldThroughConflictException() {
    var tournamentNotFoundHorse = generateValidTournamentDetailDto();
    tournamentNotFoundHorse.participants().removeLast();
    tournamentNotFoundHorse.participants().add(new HorseDetailDto(
        -500L,
        "HorseNotFound",
        Sex.MALE,
        LocalDate.of(2024, 3, 14),
        1.74f,
        500,
        null
    ));
    assertThrows(ConflictException.class,
        () -> tournamentService.create(tournamentNotFoundHorse));
  }


  @Test
  public void createTournamentWithStartDateAfterEndDataShouldThrowValidationException() {
    var validTournamentDto = generateValidTournamentDetailDto();
    var tournamentStartAfterEnd = validTournamentDto.withStartDate(2024, 3, 14).withEndDate(2024, 3, 13);
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentStartAfterEnd));
  }

  @Test
  public void createTournamentWithStartDateEqualEndDataShouldReturnCreatedTournament() {
    var validTournamentDto = generateValidTournamentDetailDto();
    var tournamentStartEqualEnd = validTournamentDto.withStartDate(2024, 3, 14).withEndDate(2024, 3, 14);
    var createdTournament = assertDoesNotThrow(() -> tournamentService.create(tournamentStartEqualEnd));
    assertNotNull(createdTournament);
    assertInstanceOf(TournamentDetailDto.class, createdTournament);
    assertAll(
        "Validating created tournament properties",
        () -> assertEquals(tournamentStartEqualEnd.id(), createdTournament.id()),
        () -> assertEquals(tournamentStartEqualEnd.name(), createdTournament.name()),
        () -> assertEquals(tournamentStartEqualEnd.startDate(), createdTournament.startDate()),
        () -> assertEquals(tournamentStartEqualEnd.endDate(), createdTournament.endDate()),
        () -> assertArrayEquals(tournamentStartEqualEnd.participants().stream().map(HorseDetailDto::id).toArray(Long[]::new),
            createdTournament.participants().stream().map(HorseDetailDto::id).toArray(Long[]::new))
    );
  }

  @Test
  public void isHorseParticipantInAnyTournamentHorseParticipatesOnceShouldReturnTrue() {
    boolean res = assertDoesNotThrow(() -> tournamentService.isHorseParticipantInAnyTournament(-2L));
    assertTrue(res);
  }
  @Test
  public void isHorseParticipantInAnyTournamentHorseParticipatesTwiceShouldReturnTrue() {
    boolean res = assertDoesNotThrow(() -> tournamentService.isHorseParticipantInAnyTournament(-1L));
    assertTrue(res);
  }

  @Test
  public void isHorseParticipantInAnyTournamentHorseIsNotParticipantShouldReturnFalse() {
    boolean res = assertDoesNotThrow(() -> tournamentService.isHorseParticipantInAnyTournament(-32L));
    assertFalse(res);
  }

  @Test
  public void isHorseParticipantInAnyTournamentNonExistentHorseShouldReturnFalse() {
    boolean res = assertDoesNotThrow(() -> tournamentService.isHorseParticipantInAnyTournament(-33L));
    assertFalse(res);
  }
}
