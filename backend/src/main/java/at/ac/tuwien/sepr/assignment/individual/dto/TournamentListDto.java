package at.ac.tuwien.sepr.assignment.individual.dto;

import java.time.LocalDate;

/**
 * DTO class for lists of tournaments in search view.
 */
public record TournamentListDto(
    Long id,
    String name,
    LocalDate start_date,
    LocalDate end_date
    ) { }
