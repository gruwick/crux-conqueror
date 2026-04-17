package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cruxconqueror.crux_conqueror.model.ForumPost;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.ForumPostRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import com.cruxconqueror.crux_conqueror.model.FriendRequest;
import com.cruxconqueror.crux_conqueror.repository.FriendRequestRepo;

@Controller
@RequestMapping("/community")
public class CommunityController {

    private final ForumPostRepo postRepo;
    private final UserRepo userRepo;
    private final FriendRequestRepo friendRequestRepo;

    public CommunityController(ForumPostRepo postRepo, UserRepo userRepo, FriendRequestRepo friendRequestRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.friendRequestRepo = friendRequestRepo;
    }

    @GetMapping
    public String forum(@RequestParam(name = "scope", defaultValue = "global") String scope,
                        Model model,
                        Principal principal) {

        List<ForumPost> posts;

        if ("friends".equals(scope) && principal != null) {
            User currentUser = userRepo.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

            List<FriendRequest> acceptedSent = friendRequestRepo.findBySenderAndStatusOrderByCreatedAtDesc(currentUser, "ACCEPTED");
            List<FriendRequest> acceptedReceived = friendRequestRepo.findByReceiverAndStatusOrderByCreatedAtDesc(currentUser, "ACCEPTED");

            List<String> usernames = new ArrayList<>();
            usernames.add(currentUser.getUsername());

            for (FriendRequest request : acceptedSent) {
                if (request.getReceiver() != null && request.getReceiver().getUsername() != null) {
                    usernames.add(request.getReceiver().getUsername());
                }
            }

            for (FriendRequest request : acceptedReceived) {
                if (request.getSender() != null && request.getSender().getUsername() != null) {
                    usernames.add(request.getSender().getUsername());
                }
            }

            posts = postRepo.findByUserUsernameInOrderByCreatedAtDesc(usernames);
        } else {
            posts = postRepo.findAllByOrderByCreatedAtDesc();
        }

        model.addAttribute("posts", posts);
        model.addAttribute("scope", scope);
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