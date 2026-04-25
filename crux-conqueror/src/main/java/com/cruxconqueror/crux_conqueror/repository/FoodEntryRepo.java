package com.cruxconqueror.crux_conqueror.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.FoodEntry;
import com.cruxconqueror.crux_conqueror.model.User;

public interface FoodEntryRepo extends JpaRepository<FoodEntry, Long> {
    List<FoodEntry> findByUserOrderByEntryDateTimeDesc(User user);

    List<FoodEntry> findByUserAndEntryDateTimeBetweenOrderByEntryDateTimeDesc(
            User user, LocalDateTime start, LocalDateTime end);

    Optional<FoodEntry> findFirstByUserOrderByEntryDateTimeDesc(User user);

}
