package at.ac.tuwien.sepr.assignment.individual.dto;

public record StandingsTreeDto(
    StandingsTreeDto[] branches,  // contains exactly two branches
    ParticipantDetailDto thisParticipant) {

  public StandingsTreeDto withBranches(StandingsTreeDto[] branches) {
    return new StandingsTreeDto(branches, this.thisParticipant);
  }

  public StandingsTreeDto withThisParticipant(ParticipantDetailDto participant) {
    return new StandingsTreeDto(this.branches, participant);
  }
}
