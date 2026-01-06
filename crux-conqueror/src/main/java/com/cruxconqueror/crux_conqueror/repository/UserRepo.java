package com.cruxconqueror.crux_conqueror.repository;
import com.cruxconqueror.crux_conqueror.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long>{
    Optional<User> findByUsername(String username);
}
