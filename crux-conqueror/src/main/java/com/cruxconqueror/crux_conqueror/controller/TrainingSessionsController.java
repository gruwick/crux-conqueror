package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cruxconqueror.crux_conqueror.model.TrainingSessions;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.TrainingSessionsRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
/**Controller for managing training sessions
 * 
 * Handles:
 * Viewing sessions
 * Creating and editing sessions
 * deleting and archiving sessions
 */
@Controller
@RequestMapping("/sessions")
public class TrainingSessionsController {

    private final TrainingSessionsRepo sessionsRepo;
    private final UserRepo userRepo;

    public TrainingSessionsController(TrainingSessionsRepo sessionsRepo, UserRepo userRepo) {
        this.sessionsRepo = sessionsRepo;
        this.userRepo = userRepo;
    }
    // Allow listing of sessions for current user
    //Displays the users training sessions
    //Allows toggle betwen archived and active sessions
    @GetMapping
    public String list(
            @RequestParam(name = "showArchived", required = false, defaultValue = "false") boolean showArchived,
            Model model,
            Principal principal) {

        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));
        //Retrive archived or active sessions
        List<TrainingSessions> sessions = showArchived
                ? sessionsRepo.findByUserAndArchivedTrueOrderBySessionDateDesc(user)
                : sessionsRepo.findByUserAndArchivedFalseOrderBySessionDateDesc(user);

        model.addAttribute("sessions", sessions);
        model.addAttribute("showArchived", showArchived);

        return "sessions/list";
    }

    //Displays the form for creating a new training session
    @GetMapping("/new")
    public String newSession(Model model) {
        TrainingSessions session = new TrainingSessions();
        session.setSessionDate(LocalDateTime.now());
        model.addAttribute("session", session);
        model.addAttribute("formTitle", "Log a training session");
        model.addAttribute("formAction", "/sessions");
        model.addAttribute("submitText", "Save session");
        return "sessions/new";
    }
    //Handles the creation of a new training session
    @PostMapping
    public String create(@ModelAttribute("session") TrainingSessions session, Principal principal) {
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));
        //Basic validation
        if (session.getSessionType() == null || session.getSessionType().isBlank()) {
            throw new IllegalArgumentException("Session type is required");
        }
        if (session.getDurationMinutes() == null || session.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        if (session.getIntensity() == null || session.getIntensity() < 1 || session.getIntensity() > 10) {
            throw new IllegalArgumentException("Intensity must be between 1 and 10");
        }
        //Ensure a session date is set
        if (session.getSessionDate() == null) {
            session.setSessionDate(LocalDateTime.now());
        }
        session.setUser(user);
        //Defaults options to 0
        if (session.getAttemptsTotal() == null)
            session.setAttemptsTotal(0);
        if (session.getTopsTotal() == null)
            session.setTopsTotal(0);
        if (session.getFlashesTotal() == null)
            session.setFlashesTotal(0);

        sessionsRepo.save(session);

        return "redirect:/sessions";
    }
    //Deletes a training session
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Principal principal) {
        TrainingSessions session = requireOwnedSession(id, principal);
        sessionsRepo.delete(session);
        return "redirect:/sessions";
    }
    //Displays an edit form for training sessions
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, Principal principal) {
        TrainingSessions session = requireOwnedSession(id, principal);
        model.addAttribute("session", session);

        model.addAttribute("formTitle", "Edit training session");
        model.addAttribute("formAction", "/sessions/" + id + "/edit");
        model.addAttribute("submitText", "Update session");

        return "sessions/new";
    }
    //Handles updating the training session
    @PostMapping("/{id}/edit")
    public String editSubmit(@PathVariable Long id,
            @ModelAttribute("session") TrainingSessions updated,
            Principal principal) {

        TrainingSessions existing = requireOwnedSession(id, principal);
        //Validate updated values
        if (updated.getSessionType() == null || updated.getSessionType().isBlank()) {
            throw new IllegalArgumentException("Session type is required");
        }
        if (updated.getDurationMinutes() == null || updated.getDurationMinutes() <= 0) {
            throw new IllegalArgumentException("Duration must be greater than 0");
        }
        if (updated.getIntensity() == null || updated.getIntensity() < 1 || updated.getIntensity() > 10) {
            throw new IllegalArgumentException("Intensity must be between 1 and 10");
        }
        if (updated.getSessionDate() == null) {
            updated.setSessionDate(existing.getSessionDate());
        }
        //Update session fields
        existing.setSessionType(updated.getSessionType());
        existing.setSessionDate(updated.getSessionDate());
        existing.setDurationMinutes(updated.getDurationMinutes());
        existing.setIntensity(updated.getIntensity());

        existing.setHighestGrade(updated.getHighestGrade());
        existing.setAttemptsTotal(updated.getAttemptsTotal() == null ? 0 : updated.getAttemptsTotal());
        existing.setTopsTotal(updated.getTopsTotal() == null ? 0 : updated.getTopsTotal());
        existing.setFlashesTotal(updated.getFlashesTotal() == null ? 0 : updated.getFlashesTotal());
        existing.setNotes(updated.getNotes());

        sessionsRepo.save(existing);

        return "redirect:/sessions";
    }
    //Archive session instead of deleting
    @PostMapping("/{id}/archive")
    public String archive(@PathVariable Long id, Principal principal) {
        TrainingSessions session = requireOwnedSession(id, principal);
        session.setArchived(true);
        session.setArchivedAt(LocalDateTime.now());
        sessionsRepo.save(session);
        return "redirect:/sessions";
    }
    //Restores an archived session
    @PostMapping("/{id}/unarchive")
    public String unarchive(@PathVariable Long id, Principal principal) {
        TrainingSessions session = requireOwnedSession(id, principal);
        session.setArchived(false);
        session.setArchivedAt(null);
        sessionsRepo.save(session);
        return "redirect:/sessions?showArchived=true";
    }
    //Bulk delted multiple sessions
    @PostMapping("/bulk-delete")
    public String bulkDelete(@RequestParam(name = "ids", required = false) List<Long> ids, Principal principal) {
        if (ids == null || ids.isEmpty())
            return "redirect:/sessions";

        for (Long id : ids) {
            TrainingSessions s = requireOwnedSession(id, principal);
            sessionsRepo.delete(s);
        }
        return "redirect:/sessions";
    }   
    //Bulk archive many sessions
    @PostMapping("/bulk-archive")
    public String bulkArchive(@RequestParam(name = "ids", required = false) List<Long> ids, Principal principal) {
        if (ids == null || ids.isEmpty())
            return "redirect:/sessions";

        for (Long id : ids) {
            TrainingSessions s = requireOwnedSession(id, principal);
            s.setArchived(true);
            s.setArchivedAt(LocalDateTime.now());
            sessionsRepo.save(s);
        }
        return "redirect:/sessions";
    }
    /** A helper method to ensure sessions belong to current user
     * Prevents other users from accessing or modifying another users sessions
     */
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