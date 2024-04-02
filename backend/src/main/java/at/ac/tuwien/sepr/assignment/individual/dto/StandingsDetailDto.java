package at.ac.tuwien.sepr.assignment.individual.dto;

import java.util.List;

public record StandingsDetailDto(
    Long id,
    String name,
    List<ParticipantDetailDto> participants,
    StandingsTreeDto tree
) {}
