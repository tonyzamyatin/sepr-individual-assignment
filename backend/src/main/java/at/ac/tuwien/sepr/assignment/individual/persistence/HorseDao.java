package at.ac.tuwien.sepr.assignment.individual.persistence;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Breed;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;

import java.util.Collection;
import java.util.Set;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {

  /**
   * Get the horses that match the given search parameters.
   * Parameters that are {@code null} are ignored.
   * The name is considered a match, if the given parameter is a substring of the field in horse.
   *
   * @param searchParameters the parameters to use in searching.
   * @return the horses where all given parameters match.
   */
  Collection<Horse> search(HorseSearchDto searchParameters);


  /**
   * Get the horses, that match one of the given IDs.
   *
   * @param horseIds the set of IDs to find horses for.
   * @return the horses with an ID in {@code horseIds}
   */
  Collection<Horse> findHorsesById(Set<Long> horseIds);

  /**
   * Create a new horse with the data given in {@code horse}. Horse will be created without specific breed if the {@link Breed} of the {@link HorseDetailDto}
   * is null.
   *
   * @param horse the new horse to create
   * @return the newly created horse
   */
  Horse create(HorseDetailDto horse);

  /**
   * Update the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   */
  Horse update(HorseDetailDto horse);

  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;

  /**
   * Delete a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to delete
   */
  void delete(long id);

}
