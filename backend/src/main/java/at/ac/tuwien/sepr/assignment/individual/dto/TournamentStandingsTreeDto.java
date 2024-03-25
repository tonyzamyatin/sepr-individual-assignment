package at.ac.tuwien.sepr.assignment.individual.dto;

public record TournamentStandingsTreeDto(
    TournamentStandingsTreeDto branches,
    TournamentParticipantDetailDto thisParticipant
) { }
