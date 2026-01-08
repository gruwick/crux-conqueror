package com.cruxconqueror.crux_conqueror.model;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Training_Sessions")
public class TrainingSessions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Session_ID")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "User_ID", nullable = false)
    private User user;

    @Column(name="Session_Date", nullable=false)
    private LocalDateTime sessionDate;

    @Column(name = "Session_Type", nullable = false, length =20)
    private String sessionType; // Some of the types that will be available later; Bouldering, Strength, FingerBoard

    @Min(1)
    @Max(600)
    @Column(name = "Duration_Minutes", nullable = false)
    private Integer durationMinutes;
    
    @Min(1)
    @Max(10)
    @Column(name = "Intensity", nullable = false)
    private Integer intensity;

    //I want to split up my columns to have specific ones for Bouldering, Strength and Fingerboarding and will mark them

    //Bouldering columns

    @Column(name = "Highest_Grade", length= 17)
    private String highestGrade;

    @Min(0)
    @Column(name = "Attempts_Total")
    private Integer attemptsTotal;

    @Min(0)
    @Column(name = "Tops_Total")
    private Integer topsTotal;

    @Min(0)
    @Column(name = "Flashes_Total")
    private Integer flashesTotal;

    @Column(name = "Notes", columnDefinition ="TEXT")
    private String notes;

    //Bouldering per grade mapped to BoulderGradeStat

    @OneToMany(mappedBy = "session, cascasde = CascadeType.ALL, orphanRemoval = true")
    private List<BoulderingGradesStat> gradeStats = new ArrayList<>();

}
