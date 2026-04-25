package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.ForumFavourite;
import com.cruxconqueror.crux_conqueror.model.ForumPost;
import com.cruxconqueror.crux_conqueror.model.User;

public interface ForumFavouriteRepo extends JpaRepository<ForumFavourite, Long> {
    Optional<ForumFavourite> findByPostAndUser(ForumPost post, User user);

    List<ForumFavourite> findByUser(User user);
}
