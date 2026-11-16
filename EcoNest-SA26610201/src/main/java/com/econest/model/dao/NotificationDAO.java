package com.econest.model.dao;
import com.econest.model.Notification;
import java.util.List;
public interface NotificationDAO {
 int save(int assignmentId, String email, String subject, String body);
 void markSent(int id);
 void markFailed(int id, String reason);
 List<Notification> findAll();
 List<Notification> findByAssignmentId(int assignmentId);
}
