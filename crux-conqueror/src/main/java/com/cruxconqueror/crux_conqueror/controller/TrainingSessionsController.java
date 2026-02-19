package com.cruxconqueror.crux_conqueror.controller;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cruxconqueror.crux_conqueror.model.BoulderingGradesStat;
import com.cruxconqueror.crux_conqueror.model.TrainingSessions;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.TrainingSessionsRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
@RequestMapping("/sessions")
public class TrainingSessionsController {

    private final TrainingSessionsRepo sessionsRepo;
    private final UserRepo userRepo;

    public TrainingSessionsController(TrainingSessionsRepo sessionsRepo, UserRepo userRepo) {
        this.sessionsRepo = sessionsRepo;
        this.userRepo = userRepo;
    }
    //Allow listing of sessions for current user

    @GetMapping
    public String list(Model model, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        List<TrainingSessions> sessions = sessionsRepo.findByUserOrderBySessionDateDesc(user);
        model.addAttribute("sessions", sessions);

        return "sessions/list";
    }
    // for creating form
    @GetMapping("/new")
    public String newSession(Model model) {
        TrainingSessions session = new TrainingSessions();
        session.setSessionDate(LocalDateTime.now()); 
        model.addAttribute("session", session);
        return "sessions/new";
    }
    
    @PostMapping
    public String create(@ModelAttribute("session") TrainingSessions session, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        if (session.getSessionType() == null || session.getSessionType().isBlank()) {
            throw new IllegalArgumentException("Session type is required");
        }
        if (session.getDurationMinutes() == null || session.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        if (session.getIntensity() == null || session.getIntensity() < 1 || session.getIntensity() > 10) {
            throw new IllegalArgumentException("Intensity must be between 1 and 10");
        }
        if (session.getSessionDate() == null) {
            session.setSessionDate(LocalDateTime.now());
        }  
        session.setUser(user);

        if(session.getAttemptsTotal() == null) session.setAttemptsTotal(0);
        if(session.getTopsTotal() == null) session.setTopsTotal(0);
        if(session.getFlashesTotal() == null) session.setFlashesTotal(0);

        TrainingSessions saved = sessionsRepo.save(session);

        if ("BOULDERING".equalsIgnoreCase(saved.getSessionType())) {
            return "redirect:/sessions/" + saved.getId() + "/grades/new";
        }

        return "redirect:/sessions";
}
    
    @GetMapping("/{id}/grades/new")
    public String newGradeRow(@PathVariable Long id, Model model, Principal principal) {
        TrainingSessions session = requireOwnedSession(id, principal);
        model.addAttribute("session", session);

        BoulderingGradesStat row = new BoulderingGradesStat();
        model.addAttribute("row", row);

        return "sessions/grade-new";
    }

  
    @PostMapping("/{id}/grades")
    public String addGradeRow(@PathVariable Long id,
                              @ModelAttribute("row") BoulderingGradesStat row,
                              Principal principal) {

        TrainingSessions session = requireOwnedSession(id, principal);

        if (row.getGrade() == null || row.getGrade().isBlank()) {
            throw new IllegalArgumentException("Grade is required");
        }
        if (row.getAttempts() == null || row.getAttempts() < 0) {
            throw new IllegalArgumentException("Attempts must be 0 or more");
        }
        if (row.getTops() == null || row.getTops() < 0) {
            throw new IllegalArgumentException("Tops must be 0 or more");
        }

        row.setSession(session);
        session.getGradeStats().add(row);

        sessionsRepo.save(session);

        return "redirect:/sessions/" + id + "/grades/new";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal){
        TrainingSessions session = requireOwnedSession(id, principal);
        sessionsRepo.delete(session);
        return "redirect:/sessions";
    }
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        TrainingSessions session = requireOwnedSession(id, principal);
        model.addAttribute("session", session);

        return "sessions/edit";
    }
   /*  @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id, 
                             @ModelAttribute("session") TrainingSessions updated, Principal principal) {
        
        return ;
    } */
    

    private TrainingSessions requireOwnedSession(Long id, Principal principal) {
        TrainingSessions session = sessionsRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.getUser() == null || session.getUser().getUsername() == null) {
            return session;
        }

        if (!session.getUser().getUsername().equals(principal.getName())) {
            throw new IllegalArgumentException("You do not have access to this session");
        }
        return session;
    }
}