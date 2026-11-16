package com.econest.model.dao;
import com.econest.model.Notification;
import com.econest.model.exception.DatabaseException;
import java.sql.*;
import java.util.*;
public class NotificationDAOImpl implements NotificationDAO {
 public int save(int assignmentId,String email,String subject,String body) {
  try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(
    "INSERT INTO notification(assignment_id,recipient_email,subject,message) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
   ps.setInt(1,assignmentId);ps.setString(2,email);ps.setString(3,subject);ps.setString(4,body);ps.executeUpdate();
   try(ResultSet rs=ps.getGeneratedKeys()){if(rs.next())return rs.getInt(1);throw new SQLException("No notification ID returned");}
  }catch(SQLException e){throw new DatabaseException("Cannot save notification: "+e.getMessage(),e);}
 }
 public void markSent(int id){change(id,"SENT",null);}
 public void markFailed(int id,String reason){change(id,"FAILED",reason);}
 private void change(int id,String status,String error){
  try(Connection c=DatabaseConnection.getConnection();PreparedStatement ps=c.prepareStatement(
   "UPDATE notification SET status=?, error_message=?, sent_at=CASE WHEN ?='SENT' THEN CURRENT_TIMESTAMP ELSE NULL END WHERE notification_id=?")){
   ps.setString(1,status);ps.setString(2,error);ps.setString(3,status);ps.setInt(4,id);ps.executeUpdate();
  }catch(SQLException e){throw new DatabaseException("Cannot update notification: "+e.getMessage(),e);}
 }
 public List<Notification> findAll(){return query("SELECT * FROM notification ORDER BY notification_id DESC",null);}
 public List<Notification> findByAssignmentId(int id){return query("SELECT * FROM notification WHERE assignment_id=? ORDER BY notification_id DESC",id);}
 private List<Notification> query(String sql,Integer id){List<Notification> list=new ArrayList<>();
  try(Connection c=DatabaseConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql)){
   if(id!=null)ps.setInt(1,id);
   try(ResultSet rs=ps.executeQuery()){while(rs.next())list.add(new Notification(rs.getInt("notification_id"),rs.getInt("assignment_id"),rs.getString("recipient_email"),rs.getString("subject"),rs.getString("message"),rs.getString("status"),rs.getString("created_at"),rs.getString("sent_at"),rs.getString("error_message")));}
  }catch(SQLException e){throw new DatabaseException("Cannot load notifications: "+e.getMessage(),e);}return list;
 }
}
