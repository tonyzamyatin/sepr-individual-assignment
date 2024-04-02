package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.TestUtility;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.ParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.ParticipantMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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

  @Autowired
  TournamentParticipantService participantService;

  @Autowired
  ParticipantMapper participantMapper;

  @Autowired
  TestUtility testUtil;

  @Test
  public void createdTournamentWithValidDtoShouldReturnCreatedTournament() {
    var validTournamentDto = testUtil.generateValidTournamentDetailDto();

    var createdTournament = assertDoesNotThrow(() -> tournamentService.create(validTournamentDto));
    assertNotNull(createdTournament);
    assertInstanceOf(TournamentDetailDto.class, createdTournament);
    assertAll(
        "createdTournament",
        () -> assertEquals(validTournamentDto.id(), createdTournament.id()),
        () -> assertEquals(validTournamentDto.name(), createdTournament.name()),
        () -> assertEquals(validTournamentDto.startDate(), createdTournament.startDate()),
        () -> assertEquals(validTournamentDto.endDate(), createdTournament.endDate()),
        // assert that both lists contain the same elements in the same order
        () -> assertEquals(validTournamentDto.participants(), createdTournament.participants())
    );
  }

  @Test
  public void createTournamentWithNullAsNameShouldThrowValidationException() {
    var validTournamentDto = testUtil.generateValidTournamentDetailDto();
    var tournamentWithNameNull = validTournamentDto.withName(null);
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNameNull));
  }

  @Test
  public void createTournamentWithEmptyStringAsNameShouldThrowValidationException() {
    var validTournamentDto = testUtil.generateValidTournamentDetailDto();
    var tournamentWithNameNull = validTournamentDto.withName("");
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNameNull));
  }

  @Test
  public void createTournamentWithSevenHorsesShouldThrowValidationException() {
    var tournamentWithSevenHorses = testUtil.generateValidTournamentDetailDto();
    tournamentWithSevenHorses.participants().removeLast();
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithSevenHorses));
  }

  @Test
  public void createTournamentWithNineHorsesShouldThrowValidationException() {
    var tournamentWithNineHorses = testUtil.generateValidTournamentDetailDto();
    tournamentWithNineHorses.participants().add(new ParticipantDetailDto(
        null,
        null,
        null,
        null,
        null
    ));
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNineHorses));
  }

  @Test
  public void createTournamentWhereOneHorseIsNullShouldThrowValidationException() {
    var tournamentWithNullHorses = testUtil.generateValidTournamentDetailDto();
    tournamentWithNullHorses.participants().removeLast();
    tournamentWithNullHorses.participants().add(null);
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNullHorses));
  }

  @Test
  public void createTournamentWhereParticipantsListIsNullShouldThrowValidationException() {
    var tournamentWithNullParticipants = testUtil.generateValidTournamentDetailDto().withParticipants(null);
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentWithNullParticipants));
  }

  @Test
  public void createTournamentWhereOneHorseIsNotInDbShouldThrowConflictException() {
    var tournamentNotFoundHorse = testUtil.generateValidTournamentDetailDto();
    tournamentNotFoundHorse.participants().removeLast();
    tournamentNotFoundHorse.participants().add(new ParticipantDetailDto(
        -38L,
        "HorseNotFound",
        LocalDate.of(2024, 3, 14),
        1,
        2
        
    ));
    assertThrows(ConflictException.class,
        () -> tournamentService.create(tournamentNotFoundHorse));
  }


  @Test
  public void createTournamentWithStartDateAfterEndDataShouldThrowValidationException() {
    var validTournamentDto = testUtil.generateValidTournamentDetailDto();
    var tournamentStartAfterEnd = validTournamentDto.withStartDate(2024, 3, 14).withEndDate(2024, 3, 13);
    assertThrows(ValidationException.class,
        () -> tournamentService.create(tournamentStartAfterEnd));
  }

  @Test
  public void createTournamentWithStartDateEqualEndDataShouldReturnCreatedTournament() {
    var validTournamentDto = testUtil.generateValidTournamentDetailDto();
    var tournamentStartEqualEnd = validTournamentDto.withStartDate(2024, 3, 14).withEndDate(2024, 3, 14);
    var createdTournament = assertDoesNotThrow(() -> tournamentService.create(tournamentStartEqualEnd), "Tournament creation should not throw any exceptions");
    assertNotNull(createdTournament);
    assertInstanceOf(TournamentDetailDto.class, createdTournament);
    assertAll(
        "Validating created tournament properties",
        () -> assertEquals(tournamentStartEqualEnd.id(), createdTournament.id()),
        () -> assertEquals(tournamentStartEqualEnd.name(), createdTournament.name()),
        () -> assertEquals(tournamentStartEqualEnd.startDate(), createdTournament.startDate()),
        () -> assertEquals(tournamentStartEqualEnd.endDate(), createdTournament.endDate()),
        // assert that both lists contain the same elements in the same order
        () -> assertEquals(validTournamentDto.participants(), createdTournament.participants())
    );
  }

  @Test
  public void isHorseParticipantInAnyTournamentHorseParticipatesOnceShouldReturnTrue() {
    boolean res = assertDoesNotThrow(() -> participantService.isHorseParticipantInAnyTournament(-2L));
    assertTrue(res);
  }
  @Test
  public void isHorseParticipantInAnyTournamentHorseParticipatesTwiceShouldReturnTrue() {
    boolean res = assertDoesNotThrow(() -> participantService.isHorseParticipantInAnyTournament(-8L));
    assertTrue(res);
  }

  @Test
  public void isHorseParticipantInAnyTournamentHorseIsNotParticipantShouldReturnFalse() {
    boolean res = assertDoesNotThrow(() -> participantService.isHorseParticipantInAnyTournament(-32L));
    assertFalse(res);
  }

  @Test
  public void isHorseParticipantInAnyTournamentNonExistentHorseShouldReturnFalse() {
    boolean res = assertDoesNotThrow(() -> participantService.isHorseParticipantInAnyTournament(-33L));
    assertFalse(res);
  }

  @Test
  public void generateFirstRoundForGeneraliOpenShouldReturnValidStandingsDetailDto() throws NotFoundException {
    var tournament = tournamentService.getTournament(-10);
    var participantsSortedByEntryNumber = tournament.participants().stream().sorted(Comparator.comparing(ParticipantDetailDto::entryNumber)).toList();
    var participantsExpectedCrossTableSort = List.of(
        participantsSortedByEntryNumber.get(2).withEntryNumber(1).withRoundReached(0),
        participantsSortedByEntryNumber.get(1).withEntryNumber(2).withRoundReached(0),
        participantsSortedByEntryNumber.get(6).withEntryNumber(3).withRoundReached(0),
        participantsSortedByEntryNumber.get(7).withEntryNumber(4).withRoundReached(0),
        participantsSortedByEntryNumber.get(0).withEntryNumber(5).withRoundReached(0),
        participantsSortedByEntryNumber.get(5).withEntryNumber(6).withRoundReached(0),
        participantsSortedByEntryNumber.get(4).withEntryNumber(7).withRoundReached(0),
        participantsSortedByEntryNumber.get(3).withEntryNumber(8).withRoundReached(0)
    );
    var tournamentWithSortedParticipants = tournament.withParticipants(participantsExpectedCrossTableSort);
    var expectedStandingsTree = testUtil.generateValidTournamentStandings(tournamentWithSortedParticipants).tree();
    var standings = assertDoesNotThrow(() -> tournamentService.generateFirstRound(tournament.id()));
    assertNotNull(standings);
    assertAll(
        "Validating generated standings properties",
        () -> assertEquals(tournament.id(), standings.id()),
        () -> assertEquals(tournament.name(), standings.name()),
        () -> assertEquals(participantsExpectedCrossTableSort, standings.participants()),
        () -> assertThat(standings.tree()).usingRecursiveComparison().isEqualTo(expectedStandingsTree)
    );
  }

}
