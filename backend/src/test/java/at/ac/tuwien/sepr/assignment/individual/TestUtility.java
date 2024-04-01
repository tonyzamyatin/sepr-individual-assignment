package at.ac.tuwien.sepr.assignment.individual;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentParticipantDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.dto.TournamentStandingsTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestUtility {

  @Autowired
  HorseService horseService;

  /**
   * Generates a valid TournamentDetailDto with 8 participants, each of which is a horse with id -1 to -8, entry number 1 to 8 and round reached 0.
   * The other information of the participants is sourced from the test database using the horse service.
   * The tournament id is -10, the name is "Test Tournament 1", the start date is 2024-03-12 and the end date is 2024-03-13.
   *
   * @return the generated valid tournament detail DTO.
   */
  public TournamentDetailDto generateValidTournamentDetailDto() {
    Long tournamentId = -11L;
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
              i,
              0)
      );
    }

    return new TournamentDetailDto(
        tournamentId,
        "Test Tournament 1",
        LocalDate.of(2024, 3, 12),
        LocalDate.of(2024, 3, 13),
        testParticipantDto);
  }

  /**
   * Generates a valid TournamentStandingsDto for the given TournamentDetailDto. The standings tree is build in respect to the round reached of the
   * participants.
   *
   * @param tournamentDetails the tournament details to generate the standings for.
   * @return a valid tournament standings DTO with the same id, participants and tree as {@code tournamentDetails}.
   */
  public TournamentStandingsDto generateValidTournamentStandings(TournamentDetailDto tournamentDetails) {
    // Sort the participants based on the round reached in descending order
    List<TournamentParticipantDetailDto> sortedParticipants = tournamentDetails.participants().stream()
        .sorted(Comparator.comparing(TournamentParticipantDetailDto::roundReached).reversed())
        .collect(Collectors.toList());

    // Create the leaf branches
    List<TournamentStandingsTreeDto> leafBranches = sortedParticipants.stream()
        .map(participant -> new TournamentStandingsTreeDto(null, participant))
        .collect(Collectors.toList());

    // Build the higher levels of the tree
    List<TournamentStandingsTreeDto> currentBranches = leafBranches;
    while (currentBranches.size() > 1) {
      currentBranches = buildBranches(currentBranches);
    }

    return new TournamentStandingsDto(
        tournamentDetails.id(),
        tournamentDetails.name(),
        sortedParticipants,
        currentBranches.getFirst()
    );
  }

  private List<TournamentStandingsTreeDto> buildBranches(List<TournamentStandingsTreeDto> inputBranches) {
    List<TournamentStandingsTreeDto> outputBranches = new ArrayList<>();
    for (int i = 0; i < inputBranches.size(); i += 2) {
      TournamentStandingsTreeDto branch1 = inputBranches.get(i);
      TournamentStandingsTreeDto branch2 = inputBranches.get(i + 1);

      // The participant who reached the higher round should be the thisParticipant of the new branch
      // If both participants reached the same round or one of them is null, thisParticipant is set to null
      TournamentParticipantDetailDto thisParticipant = null;
      if (branch1.thisParticipant() != null && branch2.thisParticipant() != null) {
        if (branch1.thisParticipant().roundReached() > branch2.thisParticipant().roundReached()) {
          thisParticipant = branch1.thisParticipant();
        } else if (branch1.thisParticipant().roundReached() < branch2.thisParticipant().roundReached()) {
          thisParticipant = branch2.thisParticipant();
        }
      }

      outputBranches.add(new TournamentStandingsTreeDto(new TournamentStandingsTreeDto[] {branch1, branch2}, thisParticipant));
    }
    return outputBranches;
  }
}
