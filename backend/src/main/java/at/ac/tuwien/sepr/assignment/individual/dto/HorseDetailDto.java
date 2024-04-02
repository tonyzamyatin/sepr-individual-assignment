package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record HorseDetailDto(
    Long id,
    String name,
    Sex sex,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dateOfBirth,
    float height,
    float weight,
    BreedDto breed
) {
  public HorseDetailDto withId(long newId) {
    return new HorseDetailDto(
        newId,
        name,
        sex,
        dateOfBirth,
        height,
        weight,
        breed);
  }
}
