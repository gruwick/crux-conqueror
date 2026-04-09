package com.cruxconqueror.crux_conqueror.model;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "User_ID")
private Long id;

@Column(name = "Usernames", nullable = false, unique = true)
private String username;

@Column(name = "Emails", nullable =false, unique = true)
private String email;

@Column(name = "Password_Hash", nullable = false)
private String passwordHash;

@Column(name = "Date_Made", insertable =false, updatable = false)
private LocalDateTime dateMade;

@Column(name = "Bio", columnDefinition = "TEXT")
private String bio;

@Column(name = "Age")
private Integer age;

@Column(name = "Height_cm")
private Double heightCm;

@Column(name = "Weight_kg")
private Double weightKg;

@Column(name = "Experience_Level", length = 30)
private String experienceLevel;

@Column(name = "Goal_Type", length = 30)
private String goalType;

@Column(name = "Activity_Level", length = 30)
private String activityLevel;

@Column(name = "Bio_Visibility", length = 20)
private String bioVisibility;

@Column(name = "Age_Visibility", length = 20)
private String ageVisibility;

@Column(name = "Height_Visibility", length = 20)
private String heightVisibility;

@Column(name = "Weight_Visibility", length = 20)
private String weightVisibility;

@Column(name = "Experience_Visibility", length = 20)
private String experienceVisibility;

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
