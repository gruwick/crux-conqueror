package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.ForumFavourite;
import com.cruxconqueror.crux_conqueror.model.ForumPost;
import com.cruxconqueror.crux_conqueror.model.User;
/**
 * Repository for accessing forum favourite data
 * 
 * Supports retrieving user favourites and checking if a post has been favourites
 */
public interface ForumFavouriteRepo extends JpaRepository<ForumFavourite, Long> {
    //Returns a favourite entry for a post and user
    //used to check if a post is already favourited
    Optional<ForumFavourite> findByPostAndUser(ForumPost post, User user);
    //Returns all posts favouirte by a user
    List<ForumFavourite> findByUser(User user);
}
