package com.cruxconqueror.crux_conqueror.model;

import jakarta.persistence.*;

/** Entity representing a like on a forum post
 * 
 * Creates a relationship betwen user and a post they have liked
 */
@Entity
@Table(name = "Forum_Likes")
public class ForumLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Like_ID")
    private Long id;
    //The post that has been liked
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "Post_ID", nullable = false)
    private ForumPost post;
    //User who liked the post
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false)

    private User user;
    //Default constructor required by JPA
    public ForumLike() {
    }
    //Getters and setters
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
