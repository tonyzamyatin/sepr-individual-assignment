package at.ac.tuwien.sepr.assignment.individual.rest.errors;

import java.util.List;

public record ListErrorRestDto(
    String message,
    List<String> errors
) {
}
