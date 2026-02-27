package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cruxconqueror.crux_conqueror.model.ForumPost;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.ForumPostRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;

@Controller
@RequestMapping("/community")
public class CommunityController {

    private final ForumPostRepo postRepo;
    private final UserRepo userRepo;

    public CommunityController(ForumPostRepo postRepo, UserRepo userRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
    }

    @GetMapping
    public String forum(Model model) {
        List<ForumPost> posts = postRepo.findAllByOrderByCreatedAtDesc();
        model.addAttribute("posts", posts);

        // for the form
        model.addAttribute("newPost", new ForumPost());

        return "community/forum";
    }

    @PostMapping
    public String create(@ModelAttribute("newPost") ForumPost newPost, Principal principal) {

        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        // basic validation (same style you use elsewhere)
        if (newPost.getTitle() == null || newPost.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (newPost.getContent() == null || newPost.getContent().isBlank()) {
            throw new IllegalArgumentException("Content is required");
        }

        newPost.setUser(user);
        newPost.setCreatedAt(LocalDateTime.now());

        postRepo.save(newPost);

        return "redirect:/community";
    }
}