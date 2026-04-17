package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.ForumLike;
import com.cruxconqueror.crux_conqueror.model.ForumPost;
import com.cruxconqueror.crux_conqueror.model.User;

public interface ForumLikeRepo extends JpaRepository<ForumLike, Long> {
    Optional<ForumLike> findByPostAndUser(ForumPost post, User user);
    long countByPost(ForumPost post);
    List<ForumLike> findByUser(User user);
}
    

