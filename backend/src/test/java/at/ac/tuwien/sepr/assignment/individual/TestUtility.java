package at.ac.tuwien.sepr.assignment.individual;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class TestUtility {

  @Autowired
  HorseService horseService;

  public TournamentDetailDto generateValidTournamentDetailDto() {
    Long tournamentId = -10L;
    List<TournamentParticipantDetailDto> testParticipantDto = new ArrayList<>();
    for (int i = 1; i <= 8; i++) {
      HorseDetailDto horse = null;
      try {
        horse = horseService.getById(-i);
      } catch (NotFoundException e) {
        throw new RuntimeException(e);
      }
      testParticipantDto.add(
          new TournamentParticipantDetailDto(
              horse.id(),
              horse.name(),
              horse.dateOfBirth(),
              1,
              1)
      );
    }

    return new TournamentDetailDto(
        tournamentId,
        "Test Tournament 1",
        LocalDate.of(2024, 3, 12),
        LocalDate.of(2024, 3, 13),
        testParticipantDto);
  }
}
