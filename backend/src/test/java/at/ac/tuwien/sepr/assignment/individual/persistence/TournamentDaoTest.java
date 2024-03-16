package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static at.ac.tuwien.sepr.assignment.individual.TestUtil.generateValidTournamentDetailDto;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles({"test", "datagen"})
@SpringBootTest
public class TournamentDaoTest extends TestBase {

  @Autowired
  TournamentDao tournamentDao;

  @Autowired
  HorseDao horseDao;

  @Test
  public void createdTournamentWithValidDtoShouldReturnCreatedTournament() {

    var validTournamentDto = generateValidTournamentDetailDto();

    var createdTournament = tournamentDao.create(validTournamentDto);
    assertNotNull(createdTournament);
    assertInstanceOf(Tournament.class, createdTournament);
    assertAll(
        "createdTournament",
        () -> assertEquals(validTournamentDto.id(), createdTournament.getId()),
        () -> assertEquals(validTournamentDto.name(), createdTournament.getName()),
        () -> assertEquals(validTournamentDto.startDate(), createdTournament.getStartDate()),
        () -> assertEquals(validTournamentDto.endDate(), createdTournament.getEndDate()),
        () -> assertArrayEquals(validTournamentDto.participants().stream().map(HorseDetailDto::id).toArray(Long[]::new), createdTournament.getParticipantIds())
    );
  }
}
