package com.cruxconqueror.crux_conqueror.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.TrainingSessions;
import com.cruxconqueror.crux_conqueror.model.User;

public interface TrainingSessionsRepo extends JpaRepository<TrainingSessions, Long> {
    List<TrainingSessions> findByUserOrderBySessionDateDesc(User user);
}
