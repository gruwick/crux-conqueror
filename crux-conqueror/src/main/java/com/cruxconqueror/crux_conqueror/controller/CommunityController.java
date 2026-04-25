package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cruxconqueror.crux_conqueror.model.ForumPost;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.ForumPostRepo;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;
import com.cruxconqueror.crux_conqueror.model.FriendRequest;
import com.cruxconqueror.crux_conqueror.repository.FriendRequestRepo;
import com.cruxconqueror.crux_conqueror.model.ForumFavourite;
import com.cruxconqueror.crux_conqueror.model.ForumLike;
import com.cruxconqueror.crux_conqueror.repository.ForumFavouriteRepo;
import com.cruxconqueror.crux_conqueror.repository.ForumLikeRepo;
import com.cruxconqueror.crux_conqueror.model.ForumComment;
import com.cruxconqueror.crux_conqueror.repository.ForumCommentRepo;

@Controller
@RequestMapping("/community")
public class CommunityController {

    private final ForumPostRepo postRepo;
    private final UserRepo userRepo;
    private final FriendRequestRepo friendRequestRepo;
    private final ForumLikeRepo forumLikeRepo;
    private final ForumFavouriteRepo forumFavouriteRepo;
    private final ForumCommentRepo forumCommentRepo;

    public CommunityController(ForumPostRepo postRepo, UserRepo userRepo, FriendRequestRepo friendRequestRepo,
            ForumLikeRepo forumLikeRepo, ForumFavouriteRepo forumFavouriteRepo, ForumCommentRepo forumCommentRepo) {
        this.postRepo = postRepo;
        this.userRepo = userRepo;
        this.friendRequestRepo = friendRequestRepo;
        this.forumFavouriteRepo = forumFavouriteRepo;
        this.forumLikeRepo = forumLikeRepo;
        this.forumCommentRepo = forumCommentRepo;
    }

    @GetMapping
    public String forum(@RequestParam(name = "scope", defaultValue = "global") String scope,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            Model model,
            Principal principal) {

        User currentUser = null;
        if (principal != null) {
            currentUser = userRepo.findByUsername(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));
        }

        List<ForumPost> posts;

        if ("friends".equals(scope) && currentUser != null) {
            List<FriendRequest> acceptedSent = friendRequestRepo.findBySenderAndStatusOrderByCreatedAtDesc(currentUser,
                    "ACCEPTED");
            List<FriendRequest> acceptedReceived = friendRequestRepo
                    .findByReceiverAndStatusOrderByCreatedAtDesc(currentUser, "ACCEPTED");

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

        Set<Long> likedPostIds = new HashSet<>();
        Set<Long> favouritePostIds = new HashSet<>();
        Map<Long, Long> likeCounts = new HashMap<>();

        if (currentUser != null) {
            List<ForumLike> myLikes = forumLikeRepo.findByUser(currentUser);
            for (ForumLike like : myLikes) {
                if (like.getPost() != null && like.getPost().getId() != null) {
                    likedPostIds.add(like.getPost().getId());
                }
            }

            List<ForumFavourite> myFavourites = forumFavouriteRepo.findByUser(currentUser);
            for (ForumFavourite favourite : myFavourites) {
                if (favourite.getPost() != null && favourite.getPost().getId() != null) {
                    favouritePostIds.add(favourite.getPost().getId());
                }
            }
        }

        for (ForumPost post : posts) {
            likeCounts.put(post.getId(), forumLikeRepo.countByPost(post));
        }

        if ("likes".equals(sort)) {
            posts.sort((a, b) -> Long.compare(
                    likeCounts.getOrDefault(b.getId(), 0L),
                    likeCounts.getOrDefault(a.getId(), 0L)));
        }

        if ("favourites".equals(sort) && currentUser != null) {
            List<ForumPost> favouritePosts = new ArrayList<>();
            for (ForumPost post : posts) {
                if (favouritePostIds.contains(post.getId())) {
                    favouritePosts.add(post);
                }
            }
            posts = favouritePosts;
        }
        Map<Long, List<ForumComment>> commentsByPost = new HashMap<>();
        Map<Long, Long> commentCounts = new HashMap<>();

        for (ForumPost post : posts) {
            List<ForumComment> comments = forumCommentRepo.findByPostOrderByCreatedAtAsc(post);
            commentsByPost.put(post.getId(), comments);
            commentCounts.put(post.getId(), forumCommentRepo.countByPost(post));
        }

        model.addAttribute("posts", posts);
        model.addAttribute("scope", scope);
        model.addAttribute("sort", sort);
        model.addAttribute("likedPostIds", likedPostIds);
        model.addAttribute("favouritePostIds", favouritePostIds);
        model.addAttribute("likeCounts", likeCounts);
        model.addAttribute("newPost", new ForumPost());
        model.addAttribute("commentsByPost", commentsByPost);
        model.addAttribute("commentCounts", commentCounts);

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

    @PostMapping("/{id}/like")
    public String toggleLike(@PathVariable Long id,
            @RequestParam(name = "scope", defaultValue = "global") String scope,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            Principal principal) {

        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        ForumPost post = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        ForumLike existing = forumLikeRepo.findByPostAndUser(post, user).orElse(null);

        if (existing != null) {
            forumLikeRepo.delete(existing);
        } else {
            ForumLike like = new ForumLike();
            like.setPost(post);
            like.setUser(user);
            forumLikeRepo.save(like);
        }

        return "redirect:/community?scope=" + scope + "&sort=" + sort + "#posts";
    }

    @PostMapping("/{id}/favourite")
    public String toggleFavourite(@PathVariable Long id,
            @RequestParam(name = "scope", defaultValue = "global") String scope,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            Principal principal) {

        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        ForumPost post = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        ForumFavourite existing = forumFavouriteRepo.findByPostAndUser(post, user).orElse(null);

        if (existing != null) {
            forumFavouriteRepo.delete(existing);
        } else {
            ForumFavourite favourite = new ForumFavourite();
            favourite.setPost(post);
            favourite.setUser(user);
            forumFavouriteRepo.save(favourite);
        }

        return "redirect:/community?scope=" + scope + "&sort=" + sort + "#posts";
    }

    @PostMapping("/{id}/comment")
    public String addComment(@PathVariable Long id,
            @RequestParam String content,
            @RequestParam(name = "scope", defaultValue = "global") String scope,
            @RequestParam(name = "sort", defaultValue = "newest") String sort,
            Principal principal) {

        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged-in user not found"));

        ForumPost post = postRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (content == null || content.isBlank()) {
            return "redirect:/community?scope=" + scope + "&sort=" + sort + "#posts";
        }

        ForumComment comment = new ForumComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());

        forumCommentRepo.save(comment);

        return "redirect:/community?scope=" + scope + "&sort=" + sort + "#posts";
    }
}