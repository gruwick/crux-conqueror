package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cruxconqueror.crux_conqueror.model.ForumPost;
/** Repository for accessing forum post data
 * 
 * Provides methods for retriving posts for community feature
 * incldues global and user specific views
 */
public interface ForumPostRepo extends JpaRepository<ForumPost, Long> {
    // returns all posts ordered by most recent first
    List<ForumPost> findAllByOrderByCreatedAtDesc();
    //returns posts created by specific set of users
    //used for friends only feed
    List<ForumPost> findByUserUsernameInOrderByCreatedAtDesc(List<String> usernames);
}