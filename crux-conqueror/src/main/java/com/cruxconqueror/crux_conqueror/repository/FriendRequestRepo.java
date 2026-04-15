package com.cruxconqueror.crux_conqueror.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cruxconqueror.crux_conqueror.model.FriendRequest;
import com.cruxconqueror.crux_conqueror.model.User;
public interface FriendRequestRepo extends JpaRepository<FriendRequest, Long> {
    List<FriendRequest> findByReceiverAndStatusOrderByCreatedAtDesc(User receiver, String status);
    List<FriendRequest> findBySenderAndStatusOrderByCreatedAtDesc(User sender, String status);
    List<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    List<FriendRequest> findBySenderAndStatusOrReceiverAndStatus(User sender, String senderStatus, User receiver, String receiverStatus);
    Optional<FriendRequest> findById(Long id);
}