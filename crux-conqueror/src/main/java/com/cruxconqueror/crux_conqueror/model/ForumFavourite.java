package com.cruxconqueror.crux_conqueror.model;

import jakarta.persistence.*;

//** Entity representing users favoruited forum posts
// Creates a relationship betwen user and post they have favourited */
@Entity
@Table(name = "Forum_Favourites")
public class ForumFavourite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Favourite_ID")
    private Long id;
    //The post that has been favourited
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "Post_ID", nullable = false)
    private ForumPost post;
    //The user who favourited
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;
    //Default constructor required for JPA
    public ForumFavourite() {
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
