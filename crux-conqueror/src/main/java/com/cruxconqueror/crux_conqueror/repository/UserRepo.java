package com.cruxconqueror.crux_conqueror.repository;
import com.cruxconqueror.crux_conqueror.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Long>{
    
}
