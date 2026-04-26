package com.cruxconqueror.crux_conqueror.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.FriendRequest;
import com.cruxconqueror.crux_conqueror.model.User;
/** 
 * Repository for friend request data
 * 
 * Supports recieving requests by sender, reciever and status
 * Enabling features such as pending requests and friend filtering
 */
public interface FriendRequestRepo extends JpaRepository<FriendRequest, Long> {
    //Returns all requests recived by a user
    List<FriendRequest> findByReceiverAndStatusOrderByCreatedAtDesc(User receiver, String status);
    //returns all requests sent by a user
    List<FriendRequest> findBySenderAndStatusOrderByCreatedAtDesc(User sender, String status);
    //Check to see if a relationship already exists
    List<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    //Retrieving all active or accepted relationships
    List<FriendRequest> findBySenderAndStatusOrReceiverAndStatus(User sender, String senderStatus, User receiver,
            String receiverStatus);
    // recieves friend rewuest by id
    Optional<FriendRequest> findById(Long id);
}