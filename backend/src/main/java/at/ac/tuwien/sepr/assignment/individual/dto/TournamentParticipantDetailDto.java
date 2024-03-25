package at.ac.tuwien.sepr.assignment.individual.dto;
import java.time.LocalDate;

public record TournamentParticipantDetailDto(
    Long horseId,
    String name,
    LocalDate dateOfBirth,
    Integer entryNumber,
    Integer roundReached
) implements Comparable<TournamentParticipantDetailDto> {

  /**
   * Compares this {@code TournamentParticipantDto} with the specified {@code TournamentParticipantDto} for order.
   * Returns a negative integer, zero, or a positive integer as this object's entry number
   * is less than, equal to, or greater than the specified object's entry number.
   * <p>
   * If the entry number of this object or the object to compare is {@code null},
   * {@code null} is considered to be less than any non-null value. Two {@code null} values
   * are considered equal.
   * </p>
   *
   * @param o The {@code TournamentParticipantDto} to be compared.
   * @return A negative integer, zero, or a positive integer as this object's entry number
   *         is less than, equal to, or greater than the specified object's entry number.
   * @throws NullPointerException if the specified object is null
   */
  @Override
  public int compareTo(TournamentParticipantDetailDto o) {
    if (this.entryNumber == null) {
      return (o.entryNumber() == null) ? 0 : -1;
    }
    if (o.entryNumber() == null) {
      return 1;
    }
    return this.entryNumber.compareTo(o.entryNumber());
  }
}
