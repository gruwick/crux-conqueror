package com.cruxconqueror.crux_conqueror.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
/**
 * Entity representing a forum post created by a user
 * 
 * Stores the post along with content metadata such as author and creation timestamp
 */
@Entity
@Table(name = "Forum_Posts")
public class ForumPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Post_ID")
    private Long id;
    //User who created the post
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;
    //title of post
    @Column(name = "Title", nullable = false, length = 120)
    private String title;
    //Main body content
    @Column(name = "Content", nullable = false, columnDefinition = "TEXT")
    private String content;
    //Timestamp
    @Column(name = "Created_At", nullable = false)
    private LocalDateTime createdAt;
    //Default constructor required by JPA
    public ForumPost() {
    }
    //Getters and setters
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}