package at.ac.tuwien.sepr.assignment.individual.dto;


import java.time.LocalDate;
import java.util.List;

public record TournamentDetailDto(
    Long id,
    String name,
    LocalDate startDate,
    LocalDate endDate,
    List<TournamentParticipantDetailDto> participants

) {

  public TournamentDetailDto withId(Long newId) {
    return new TournamentDetailDto(
        newId,
        name,
        startDate,
        endDate,
        participants
    );
  }

  public TournamentDetailDto withStartDate(int year, int month, int day) {
    return withStartDate(LocalDate.of(year, month, day));
  }

  public TournamentDetailDto withStartDate(LocalDate newStartDate) {
    return new TournamentDetailDto(
        id,
        name,
        newStartDate,
        endDate,
        participants
    );
  }

  public TournamentDetailDto withEndDate(int year, int month, int day) {
    return withEndDate(LocalDate.of(year, month, day));
  }

  public TournamentDetailDto withEndDate(LocalDate newEndDate) {
    return new TournamentDetailDto(
        id,
        name,
        startDate,
        newEndDate,
        participants
    );
  }

  public TournamentDetailDto withName(String newName) {
    return new TournamentDetailDto(
        id,
        newName,
        startDate,
        endDate,
        participants
    );
  }

  public TournamentDetailDto withParticipants(List<TournamentParticipantDetailDto> newParticipants) {
    return new TournamentDetailDto(
        id,
        name,
        startDate,
        endDate,
        newParticipants
    );
  }
}
