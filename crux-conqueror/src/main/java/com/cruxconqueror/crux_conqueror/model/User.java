package com.cruxconqueror.crux_conqueror.model;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
@Id
@Column(name = "User_ID")
private Long id;

@Column(name = "Usernames", nullable = false, unique = true)
private String username;

@Column(name = "Emails", nullable =false, unique = true)
private String email;

@Column(name = "Password_Hash", nullable = false, unique = true)
private String passwordHash;

@Column(name = "Date_Made")
private LocalDateTime dateMade;

}
