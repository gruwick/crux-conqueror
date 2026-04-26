package com.cruxconqueror.crux_conqueror.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
/**
 * Entity representing bouldering grade stats for a session
 * 
 * Stores performance data such as attempts and tops for each grade
 * all within a specific training session
 */
@Entity
@Table(name = "Bouldering_Grade_Stats")
public class BoulderingGradesStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GradeStat_ID")
    private Long id;

    // Many of our grade rows can below to a single training sessiosn
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "Session_ID", nullable = false)
    private TrainingSessions session;

    // Dataa about our Grades
    @Column(name = "Grade", nullable = false, length = 20)
    private String grade;

    @Min(0)
    @Column(name = "Attempts", nullable = false)
    private Integer attempts;

    @Min(0)
    @Column(name = "Tops", nullable = false)
    private Integer tops;

    // Constructors for the above

    public BoulderingGradesStat() {
    }

    public BoulderingGradesStat(TrainingSessions session, String grade, Integer attempts, Integer tops) {
        this.session = session;
        this.grade = grade;
        this.attempts = attempts;
        this.tops = tops;
    }

    // My getters and setters

    public Long getId() {
        return id;
    }

    public TrainingSessions getSession() {
        return session;
    }

    public void setSession(TrainingSessions session) {
        this.session = session;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getAttempts() {
        return attempts;
    }

    public void setAttempts(Integer attempts) {
        this.attempts = attempts;
    }

    public Integer getTops() {
        return tops;
    }

    public void setTops(Integer tops) {
        this.tops = tops;
    }
}
