package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.TrainingSessions;
import com.cruxconqueror.crux_conqueror.model.User;
import java.time.LocalDateTime;
import java.util.Optional;
/** Repository for accessing training session data
 * 
 * Suports retrieving sessionf for users filtering archived data
 * generating stas for dashboard and leaderboard features
*/
public interface TrainingSessionsRepo extends JpaRepository<TrainingSessions, Long> {
    //returns all sessions for  a user by most recent
    List<TrainingSessions> findByUserOrderBySessionDateDesc(User user);
    //Returns active sessions
    List<TrainingSessions> findByUserAndArchivedFalseOrderBySessionDateDesc(User user);
    //returns archived sessions
    List<TrainingSessions> findByUserAndArchivedTrueOrderBySessionDateDesc(User user);
    //Returns all active sessions for a given date
    //Used by leaderboard and recent activity calculations
    List<TrainingSessions> findByArchivedFalseAndSessionDateAfter(LocalDateTime since);
    //returns the total number of sessions for a user
    long countByUserAndArchivedFalse(User user);
    //returns most recent session for a user
    Optional<TrainingSessions> findFirstByUserAndArchivedFalseOrderBySessionDateDesc(User user);

}
