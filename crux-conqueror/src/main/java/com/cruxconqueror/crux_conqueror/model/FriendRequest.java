package com.cruxconqueror.crux_conqueror.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "Friend_Requests")
public class FriendRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Request_ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "Sender_User_ID", nullable = false)
    private User sender;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "Receiver_User_ID", nullable = false)
    private User receiver;

    @Column(name = "Status", nullable = false, length = 20)
    private String status;

    @Column(name = "Created_At", nullable = false)
    private LocalDateTime createdAt;

    public FriendRequest() {
    }

    public Long getId() {
        return id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}