package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Tournament;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static at.ac.tuwien.sepr.assignment.individual.TestUtil.generateValidTournamentDetailDto;
import static org.assertj.core.api.Assertions.assertThat;
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
  public void searchByNameIaFindTwoTournaments() {
    var searchDto = new TournamentSearchDto("ia", null, null, null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament())
                .setId(-1L)
                .setName("BNP Paribas Open, Indian Wells")
                .setStartDate(LocalDate.of(2024, 3, 6))
                .setEndDate(LocalDate.of(2024, 3, 17)),
            (new Tournament())
                .setId(-2L)
                .setName("Miami Open presented by Itau")
                .setStartDate(LocalDate.of(2024, 3, 20))
                .setEndDate(LocalDate.of(2024, 3, 31))
        );
  }

  @Test
  public void searchByIntervalStart31Mar2024ShouldReturnTwoTournaments() {
    var searchDto = new TournamentSearchDto(null, LocalDate.of(2024, 3, 31), null, null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament()
                .setId(-2L)
                .setName("Miami Open presented by Itau")
                .setStartDate(LocalDate.of(2024, 3, 20))
                .setEndDate(LocalDate.of(2024, 3, 31))),
            (new Tournament())
                .setId(-3L)
                .setName("Rolex Monte-Carlo Masters")
                .setStartDate(LocalDate.of(2024, 4, 7))
                .setEndDate(LocalDate.of(2024, 4, 14))
        );
  }

  @Test
  public void searchByIntervalEnd20Mar2024ShouldReturnTwoTournaments() {
    var searchDto = new TournamentSearchDto(null, null, LocalDate.of(2024, 3, 20), null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament())
                .setId(-1L)
                .setName("BNP Paribas Open, Indian Wells")
                .setStartDate(LocalDate.of(2024, 3, 6))
                .setEndDate(LocalDate.of(2024, 3, 17)),
            (new Tournament())
                .setId(-2L)
                .setName("Miami Open presented by Itau")
                .setStartDate(LocalDate.of(2024, 3, 20))
                .setEndDate(LocalDate.of(2024, 3, 31))
        );
  }


  @Test
  public void searchByIntervalStart7Mar2024End13Apr2024ShouldReturnThreeTournaments() {
    var searchDto = new TournamentSearchDto(null, LocalDate.of(2024, 3, 7), LocalDate.of(2024, 4, 13), null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament())
                .setId(-1L)
                .setName("BNP Paribas Open, Indian Wells")
                .setStartDate(LocalDate.of(2024, 3, 6))
                .setEndDate(LocalDate.of(2024, 3, 17)),
            (new Tournament())
                .setId(-2L)
                .setName("Miami Open presented by Itau")
                .setStartDate(LocalDate.of(2024, 3, 20))
                .setEndDate(LocalDate.of(2024, 3, 31)),
            (new Tournament())
                .setId(-3L)
                .setName("Rolex Monte-Carlo Masters")
                .setStartDate(LocalDate.of(2024, 4, 7))
                .setEndDate(LocalDate.of(2024, 4, 14))
        );
  }

  @Test
  public void searchByNameIaAndByIntervalEnd19Mar2024ShouldReturnOneTournament() {
    var searchDto = new TournamentSearchDto("ia", null, LocalDate.of(2024, 3, 19), null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrder(
            (new Tournament())
                .setId(-1L)
                .setName("BNP Paribas Open, Indian Wells")
                .setStartDate(LocalDate.of(2024, 3, 6))
                .setEndDate(LocalDate.of(2024, 3, 17))
        );
  }

  @Test
  public void searchByIntervalStart7Mar2024End13Apr2024WithLimit1ShouldReturnOneTournament() {
    var searchDto = new TournamentSearchDto(null, LocalDate.of(2024, 3, 7), LocalDate.of(2024, 4, 13), 1);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments)
        .usingRecursiveFieldByFieldElementComparator()
        .containsAnyOf(
            (new Tournament())
                .setId(-1L)
                .setName("BNP Paribas Open, Indian Wells")
                .setStartDate(LocalDate.of(2024, 3, 6))
                .setEndDate(LocalDate.of(2024, 3, 17)),
            (new Tournament())
                .setId(-2L)
                .setName("Miami Open presented by Itau")
                .setStartDate(LocalDate.of(2024, 3, 20))
                .setEndDate(LocalDate.of(2024, 3, 31)),
            (new Tournament())
                .setId(-3L)
                .setName("Rolex Monte-Carlo Masters")
                .setStartDate(LocalDate.of(2024, 4, 7))
                .setEndDate(LocalDate.of(2024, 4, 14))
        );
  }

  @Test
  public void searchByIntervalEnd5Mar2024ShouldReturnNoTournaments() {
    var searchDto = new TournamentSearchDto(null, null, LocalDate.of(2024, 3, 5), null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments).isEmpty();
  }


  @Test
  public void searchByIntervalStart15Apr2024ShouldReturnNoTournaments() {
    var searchDto = new TournamentSearchDto(null, LocalDate.of(2024, 4, 15), null, null);
    var tournaments = tournamentDao.search(searchDto);
    assertNotNull(tournaments);
    assertThat(tournaments).isEmpty();
  }

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
        () -> assertEquals(validTournamentDto.endDate(), createdTournament.getEndDate())
    );
  }
}
