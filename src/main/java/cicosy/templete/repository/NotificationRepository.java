package cicosy.templete.repository;

import cicosy.templete.domain.Notification;
import cicosy.templete.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
    
    List<Notification> findByRecipientAndStatusOrderByCreatedAtDesc(User recipient, Notification.NotificationStatus status);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.recipient = :user AND n.status = 'UNREAD'")
    long countUnreadNotifications(@Param("user") User user);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient = :user AND n.status = 'UNREAD' ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotifications(@Param("user") User user);
    
    @Query("SELECT n FROM Notification n WHERE n.recipient = :user ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("user") User user);
    
    void deleteByRecipientAndStatus(User recipient, Notification.NotificationStatus status);
}