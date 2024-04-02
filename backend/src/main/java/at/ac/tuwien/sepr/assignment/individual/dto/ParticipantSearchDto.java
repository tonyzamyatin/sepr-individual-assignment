package at.ac.tuwien.sepr.assignment.individual.dto;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

public record ParticipantSearchDto(
    Long horseId,
    Long tournamentId,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate intervalStart,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate intervalEnd
) { }
