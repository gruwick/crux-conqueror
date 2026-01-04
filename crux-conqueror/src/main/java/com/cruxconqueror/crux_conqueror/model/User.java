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

public User(){}

public User(String username, String email, String passwordHash){
    this.username = username;
    this.email = email;
    this.passwordHash = passwordHash;
}
public Long getId(){return id;}
public String getUsername(){return username;}
public String getEmail(){return email;}
public String getPasswordHash(){return passwordHash;}
public LocalDateTime getDateMade(){return dateMade;}

public void setUsername(String username) {this.username = username;}
public void setEmail(String email) {this.email = email;}
public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}
}
