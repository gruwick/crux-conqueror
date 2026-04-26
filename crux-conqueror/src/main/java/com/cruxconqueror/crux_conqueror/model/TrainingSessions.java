package com.cruxconqueror.crux_conqueror.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

/** Entity representing a training session
 * 
 * Stores general session data (type, duration, intensity)
 * As well as specific bouldering performance metrics
 */
@Entity
@Table(name = "Training_Sessions")
public class TrainingSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Session_ID")
    private Long id;
    //Each session belongs to a specific user
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;
    //Date and time it took place
    @Column(name = "Session_Date", nullable = false)
    private LocalDateTime sessionDate;
    //Session type, Bouldering, Strength, fingerboarding
    @Column(name = "Session_Type", nullable = false, length = 20)
    private String sessionType;
    //Duration of the session
    @Min(1)
    @Max(600)
    @Column(name = "Duration_Minutes", nullable = false)
    private Integer durationMinutes;
    //Intensity 
    @Min(1)
    @Max(10)
    @Column(name = "Intensity", nullable = false)
    private Integer intensity;
    //Archiving
    @Column(name = "Archived", nullable = false)
    private Boolean archived = false;
    //Time archived
    @Column(name = "Archived_At")
    private LocalDateTime archivedAt;

    // Optional Bouldering columns
    //Highest grade
    @Column(name = "Highest_Grade", length = 17)
    private String highestGrade;
    //Attempts total
    @Min(0)
    @Column(name = "Attempts_Total")
    private Integer attemptsTotal;
    //tops total
    @Min(0)
    @Column(name = "Tops_Total")
    private Integer topsTotal;
    //Flashes total
    @Min(0)
    @Column(name = "Flashes_Total")
    private Integer flashesTotal;
    //Additional notes
    @Column(name = "Notes", columnDefinition = "TEXT")
    private String notes;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoulderingGradesStat> gradeStats = new ArrayList<>();
    //Default constructor required by JPA
    public TrainingSessions() {
    }

    // Constructors, getters and setters

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(LocalDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }

    public String getSessionType() {
        return sessionType;
    }

    public void setSessionType(String sessionType) {
        this.sessionType = sessionType;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public Integer getIntensity() {
        return intensity;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public String getHighestGrade() {
        return highestGrade;
    }

    public void setHighestGrade(String highestGrade) {
        this.highestGrade = highestGrade;
    }

    public Integer getAttemptsTotal() {
        return attemptsTotal;
    }

    public void setAttemptsTotal(Integer attemptsTotal) {
        this.attemptsTotal = attemptsTotal;
    }

    public Integer getTopsTotal() {
        return topsTotal;
    }

    public void setTopsTotal(Integer topsTotal) {
        this.topsTotal = topsTotal;
    }

    public Integer getFlashesTotal() {
        return flashesTotal;
    }

    public void setFlashesTotal(Integer flashesTotal) {
        this.flashesTotal = flashesTotal;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<BoulderingGradesStat> getGradeStats() {
        return gradeStats;
    }

    public void setGradeStats(List<BoulderingGradesStat> gradeStats) {
        this.gradeStats = gradeStats;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }
}
