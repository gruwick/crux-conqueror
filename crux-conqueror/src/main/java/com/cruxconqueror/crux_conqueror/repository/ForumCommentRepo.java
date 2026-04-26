package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.ForumComment;
import com.cruxconqueror.crux_conqueror.model.ForumPost;
/** Repository for accessing forum comment data
 * Provides methods for retrieving comments fro a post
 * Retrives engagement metrics */ 
public interface ForumCommentRepo extends JpaRepository<ForumComment, Long> {
    //Return all coments for a post in created by order
    List<ForumComment> findByPostOrderByCreatedAtAsc(ForumPost post);
    //Returns total number of comments per post
    long countByPost(ForumPost post);
}