package com.cruxconqueror.crux_conqueror.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.cruxconqueror.crux_conqueror.repository.FriendRequestRepo;
import com.cruxconqueror.crux_conqueror.model.FriendRequest;
import com.cruxconqueror.crux_conqueror.model.User;
import com.cruxconqueror.crux_conqueror.repository.UserRepo;

/**
 * Controller responsible for acount page
 * 
 * includes:
 * Viewing account details
 * updating user prfile and nutrition goals
 * managing friend requests
 */

@Controller
public class AccountController {
    private final UserRepo userRepo;
    private final FriendRequestRepo friendRequestRepo;

    public AccountController(UserRepo userRepo, FriendRequestRepo friendRequestRepo) {
        this.userRepo = userRepo;
        this.friendRequestRepo = friendRequestRepo;
    }

    /**loads up the account page
     * 
     * Includes
     * user details
     * Friend requests incomming and outgoing
     * Accepted requests
     * Suggested users
     */
    @GetMapping("/account")
    public String account(@RequestParam(required = false) String search, Model model, Principal principal) {
        // Ensures user is logged in
        if (principal == null) {
            return "redirect:/login";
        }
        //Get currently logged in user
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));

                //get friends requests by status
        List<FriendRequest> incomingRequests = friendRequestRepo.findByReceiverAndStatusOrderByCreatedAtDesc(user,
                "PENDING");
        List<FriendRequest> outgoingRequests = friendRequestRepo.findBySenderAndStatusOrderByCreatedAtDesc(user,
                "PENDING");
        List<FriendRequest> acceptedSent = friendRequestRepo.findBySenderAndStatusOrderByCreatedAtDesc(user,
                "ACCEPTED");
        List<FriendRequest> acceptedReceived = friendRequestRepo.findByReceiverAndStatusOrderByCreatedAtDesc(user,
                "ACCEPTED");

        // builds a list of confirmed friends
        List<User> friends = new ArrayList<>();

        for (FriendRequest request : acceptedSent) {
            if (request.getReceiver() != null) {
                friends.add(request.getReceiver());
            }
        }
        for (FriendRequest request : acceptedReceived) {
            if (request.getSender() != null) {
                friends.add(request.getSender());
            }
        }

        //Builds a list of users to exlude, pending requests your friends and yourself of course
        List<Long> blockedIds = new ArrayList<>();
        blockedIds.add(user.getId());

        for (User friend : friends) {
            blockedIds.add(friend.getId());
        }
        for (FriendRequest request : incomingRequests) {
            if (request.getSender() != null) {
                blockedIds.add(request.getSender().getId());
            }
        }
        for (FriendRequest request : outgoingRequests) {
            if (request.getReceiver() != null) {
                blockedIds.add(request.getReceiver().getId());
            }
        }
        List<User> suggestedUsers;

        // search and filter users by usernaem
        if (search != null && !search.isBlank()) {
            suggestedUsers = userRepo.findByUsernameContainingIgnoreCase(search).stream()
                    .filter(u -> u.getId() != null)
                    .filter(u -> !blockedIds.contains(u.getId()))
                    .sorted(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER))
                    .collect(Collectors.toList());
        } else {
            // else show a limited number of suggested
            suggestedUsers = userRepo.findAll().stream()
                    .filter(u -> u.getId() != null)
                    .filter(u -> !blockedIds.contains(u.getId()))
                    .sorted(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER))
                    .limit(8)
                    .collect(Collectors.toList());
        }
        //sort your friends alphabetically
        friends = friends.stream()
                .sorted(Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
        // all data to add to the view
        model.addAttribute("user", user);
        model.addAttribute("incomingRequests", incomingRequests);
        model.addAttribute("outgoingRequests", outgoingRequests);
        model.addAttribute("friends", friends);
        model.addAttribute("suggestedUsers", suggestedUsers);
        model.addAttribute("search", search);

        return "Account/account";
    }

    //Handles updating details and goals
    @PostMapping("/account")
    public String updateAccount(Principal principal, @RequestParam(required = false) String bio,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Double heightCm,
            @RequestParam(required = false) Double weightKg,
            @RequestParam(required = false) String experienceLevel,
            @RequestParam(required = false) String goalType, @RequestParam(required = false) String activityLevel,
            @RequestParam(required = false) String bioVisibility,
            @RequestParam(required = false) String ageVisibility,
            @RequestParam(required = false) String heightVisibility,
            @RequestParam(required = false) String weightVisibility,
            @RequestParam(required = false) String experienceVisibility,
            @RequestParam(required = false) String targetMode,
            @RequestParam(required = false) Integer calorieGoal,
            @RequestParam(required = false) Integer proteinGoal,
            @RequestParam(required = false) Integer carbGoal,
            @RequestParam(required = false) Integer fatGoal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User user = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));
        //update profile fields
        user.setBio(bio);
        user.setAge(age);
        user.setHeightCm(heightCm);
        user.setWeightKg(weightKg);
        user.setExperienceLevel(experienceLevel);
        user.setGoalType(goalType);
        user.setActivityLevel(activityLevel);
        //update visibility settings
        user.setBioVisibility(bioVisibility);
        user.setAgeVisibility(ageVisibility);
        user.setHeightVisibility(heightVisibility);
        user.setWeightVisibility(weightVisibility);
        user.setExperienceVisibility(experienceVisibility);
        user.setTargetMode(targetMode);
        //Auto vs manual for nutrition goals
        if ("Auto".equalsIgnoreCase(targetMode)) {
            calculateGoals(user);
        } else {
            user.setCalorieGoal(calorieGoal);
            user.setProteinGoal(proteinGoal);
            user.setCarbGoal(carbGoal);
            user.setFatGoal(fatGoal);
        }
        userRepo.save(user);

        return "redirect:/account#nutrition";
    }

    //Calculates calorie and macro targets based on user data
    private void calculateGoals(User user) {
        //ensures data is there
        if (user.getWeightKg() == null || user.getHeightCm() == null || user.getAge() == null) {
            return;
        }
        // Using Mifflin-ST Jeor equation for
        // metabolic rates
        double bmr = (10 * user.getWeightKg()) + (6.25 * user.getHeightCm()) - (5 * user.getAge()) + 5;
        double ree = bmr;
        //adjust based on activity level
        if ("Very High".equals(user.getActivityLevel()))
            ree *= 1.9;
        else if ("High".equals(user.getActivityLevel()))
            ree *= 1.725;
        else if ("Moderate".equals(user.getActivityLevel()))
            ree *= 1.55;
        else
            ree *= 1.2;
        //adjust based on goal, cut bulk etc
        String goal = user.getGoalType();
        if ("Bulk".equalsIgnoreCase(goal))
            ree += 300;
        else if ("Cut".equalsIgnoreCase(goal))
            ree -= 300;

        user.setCalorieGoal((int) Math.round(ree));

        int totalCalories = (int) Math.round(ree);
        user.setCalorieGoal(totalCalories);
        //Calculate macro split
        int proteinGrams = (int) Math.round(user.getWeightKg() * 1.8);
        int fatGrams = (int) Math.round(user.getWeightKg() * 0.8);
        int proteinCalories = proteinGrams * 4;
        int fatCalories = fatGrams * 9;
        int remainingCalories = totalCalories - (proteinCalories + fatCalories);
        if (remainingCalories < 0)
            remainingCalories = 0;
        int carbGrams = remainingCalories / 4;

        user.setProteinGoal(proteinGrams);
        user.setFatGoal(fatGrams);
        user.setCarbGoal(carbGrams);


    }
    // send friend requests to a user
    @PostMapping("/friends/request")
    public String sendFriendRequest(@RequestParam Long receiverId, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User sender = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));
        User receiver = userRepo.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        // doesnt let you send one to yourself
        if (sender.getId().equals(receiver.getId())) {
            return "redirect:/account#social";
        }
        //prevent duplicate friend requests
        boolean alreadyExists = !friendRequestRepo.findBySenderAndReceiver(sender, receiver).isEmpty()
                || !friendRequestRepo.findBySenderAndReceiver(receiver, sender).isEmpty();

        if (alreadyExists) {
            return "redirect:/account#social";
        }
        //Create and save new request
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus("PENDING");
        request.setCreatedAt(LocalDateTime.now());
        friendRequestRepo.save(request);

        return "redirect:/account#social";
    }
    //Accept friend request
    @PostMapping("/friends/{id}/accept")
    public String acceptFriendRequest(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));
        FriendRequest request = friendRequestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));
        // make sure correct user is accepting
        if (request.getReceiver() == null || !request.getReceiver().getId().equals(currentUser.getId())) {
            return "redirect:/account#social";
        }
        request.setStatus("ACCEPTED");
        friendRequestRepo.save(request);

        return "redirect:/account#social";
    }
    //Decline a friend request
    @PostMapping("/friends/{id}/decline")
    public String declineFriendRequest(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        User currentUser = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));
        FriendRequest request = friendRequestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));
        //Ensure correct user is declining
        if (request.getReceiver() == null || !request.getReceiver().getId().equals(currentUser.getId())) {
            return "redirect:/account#social";
        }
        friendRequestRepo.delete(request);

        return "redirect:/account#social";
    }
    //Cancel a send friend request
    @PostMapping("/friends/{id}/cancel")
    public String cancelFriendRequest(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User currentUser = userRepo.findByUsername(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));
        FriendRequest request = friendRequestRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found"));
        //Ensuring only the right user can cancel
        if (request.getSender() == null || !request.getSender().getId().equals(currentUser.getId())) {
            return "redirect:/account#social";
        }

        friendRequestRepo.delete(request);

        return "redirect:/account#social";
    }

}
