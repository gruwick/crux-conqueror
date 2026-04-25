package com.cruxconqueror.crux_conqueror.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Forum_Likes")
public class ForumLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Like_ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "Post_ID", nullable = false)
    private ForumPost post;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false)

    private User user;

    public ForumLike() {
    }

    public Long getId() {
        return id;
    }

    public ForumPost getPost() {
        return post;
    }

    public void setPost(ForumPost post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
