package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.BreedSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import java.util.Collection;
import java.util.Set;

public interface BreedDao {

  /**
   * Get all breeds.
   *
   * @return a collection of all stored breeds.
   */
  Collection<Breed> allBreeds();

  /**
   * Get the breeds, that match one of the given IDs.
   *
   * @param breedIds the set of IDs to find breeds for.
   * @return the breeds with an ID in {@code breedIds}
   */
  Collection<Breed> findBreedsById(Set<Long> breedIds);

  /**
   * Get the breeds that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in breed.
   *
   * @param searchParams parameters to search breeds by
   * @return the breeds where all given parameters match
   */
  Collection<Breed> search(BreedSearchDto searchParams);
}
