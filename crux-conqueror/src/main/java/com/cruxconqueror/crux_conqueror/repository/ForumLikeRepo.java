package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.ForumLike;
import com.cruxconqueror.crux_conqueror.model.ForumPost;
import com.cruxconqueror.crux_conqueror.model.User;
/** Repository for accessing forum like data
 * suports tracking user likes and calulating post popularity
 */
public interface ForumLikeRepo extends JpaRepository<ForumLike, Long> {
    //Returns a like entry for a given post and user
    //used to determin if a post has already been liked
    Optional<ForumLike> findByPostAndUser(ForumPost post, User user);
    // returns total number of likes for post
    long countByPost(ForumPost post);
    //returns all likes made by user
    List<ForumLike> findByUser(User user);
}
