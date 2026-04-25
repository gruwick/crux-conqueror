package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.ForumComment;
import com.cruxconqueror.crux_conqueror.model.ForumPost;

public interface ForumCommentRepo extends JpaRepository<ForumComment, Long> {
    List<ForumComment> findByPostOrderByCreatedAtAsc(ForumPost post);

    long countByPost(ForumPost post);
}