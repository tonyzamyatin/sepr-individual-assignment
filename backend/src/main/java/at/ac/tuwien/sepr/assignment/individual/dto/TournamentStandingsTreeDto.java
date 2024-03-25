package at.ac.tuwien.sepr.assignment.individual.dto;

public record TournamentStandingsTreeDto(
    TournamentStandingsTreeDto[] branches,  // contains exactly two branches
    TournamentParticipantDetailDto thisParticipant) {

  public TournamentStandingsTreeDto withBranches(TournamentStandingsTreeDto[] branches) {
    return new TournamentStandingsTreeDto(branches, this.thisParticipant);
  }

  public TournamentStandingsTreeDto withThisParticipant(TournamentParticipantDetailDto participant) {
    return new TournamentStandingsTreeDto(this.branches, participant);
  }
}
