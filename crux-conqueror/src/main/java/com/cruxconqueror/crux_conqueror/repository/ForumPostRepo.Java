package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cruxconqueror.crux_conqueror.model.ForumPost;

public interface ForumPostRepo extends JpaRepository<ForumPost, Long> {
    List<ForumPost> findAllByOrderByCreatedAtDesc();
}