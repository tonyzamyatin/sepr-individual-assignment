package at.ac.tuwien.sepr.assignment.individual;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestUtil {

  public static TournamentDetailDto generateValidTournamentDetailDto() {
    List<HorseDetailDto> testHorseDtos = new ArrayList<>();
    for (int i = 1; i <= 8; i++) {
      testHorseDtos.add(
          new HorseDetailDto(
              (long) -i,
              "Horse " + i,
              i % 2 == 0 ? Sex.MALE : Sex.FEMALE,
              LocalDate.of(2015, 1, i),
              150 + i,
              500 + (i * 10),
              null)
      );
    }

    return new TournamentDetailDto(
        -10L,
        "Test Tournament 1",
        LocalDate.of(2024, 3, 12),
        LocalDate.of(2024, 3, 13),
        testHorseDtos);
  }
}
