package com.cruxconqueror.crux_conqueror.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.model.User;
/** Repository for accessing foodEntry
 * 
 * Provides methods for retrieving user nutrition entries
 * Included date based filtering and recent entries
 */
public interface FoodEntryRepo extends JpaRepository<FoodEntry, Long> {
    //Returns all food entries for user, ordered by most recent
    List<FoodEntry> findByUserOrderByEntryDateTimeDesc(User user);
    /**Returns food entries withing a specified date range
     * Used for dauly and weekly nutrition calculations
     */
    List<FoodEntry> findByUserAndEntryDateTimeBetweenOrderByEntryDateTimeDesc(
            User user, LocalDateTime start, LocalDateTime end);
    //Returns the most recent food entry
    Optional<FoodEntry> findFirstByUserOrderByEntryDateTimeDesc(User user);

}
