package at.ac.tuwien.sepr.assignment.individual.dto;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record HorseSelectionDto(
    long id,
    String name,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dateOfBirth
) {
}
