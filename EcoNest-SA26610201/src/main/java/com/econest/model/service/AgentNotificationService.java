package com.econest.model.service;
import com.econest.model.Notification;
import com.econest.model.dao.*;
import com.econest.model.util.EmailConfig;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

/** Task 9: create an assignment, persist notification, send via SMTP and record result. */
public class AgentNotificationService {
 private final NotificationDAO notifications;
 public AgentNotificationService(NotificationDAO notifications){this.notifications=notifications;}
 public List<Notification> history(){return notifications.findAll();}
 public int assignAndNotify(int orderId,int agentId,LocalDate date,String address){
  if(orderId<=0||agentId<=0||date==null||address==null||address.isBlank())throw new IllegalArgumentException("Select an order and agent, date, and address.");
  int assignmentId;
  try(Connection c=DatabaseConnection.getConnection()){
   try(PreparedStatement check=c.prepareStatement("SELECT 1 FROM client_order WHERE order_id=?")){
    check.setInt(1,orderId);try(ResultSet r=check.executeQuery()){if(!r.next())throw new IllegalArgumentException("Order does not exist.");}
   }
   try(PreparedStatement check=c.prepareStatement("SELECT 1 FROM delivery_agent WHERE agent_id=?")){
    check.setInt(1,agentId);try(ResultSet r=check.executeQuery()){if(!r.next())throw new IllegalArgumentException("Agent does not exist.");}
   }
   try(PreparedStatement ps=c.prepareStatement("INSERT INTO task9_delivery_assignment(order_id,agent_id,scheduled_date,delivery_address) VALUES(?,?,?,?)",Statement.RETURN_GENERATED_KEYS)){
    ps.setInt(1,orderId);ps.setInt(2,agentId);ps.setString(3,date.toString());ps.setString(4,address.trim());ps.executeUpdate();
    try(ResultSet rs=ps.getGeneratedKeys()){if(!rs.next())throw new SQLException("Missing assignment ID");assignmentId=rs.getInt(1);}
   }
  }catch(SQLException e){throw new IllegalStateException("Could not assign delivery: "+e.getMessage(),e);}
  notifyAssignment(assignmentId);
  return assignmentId;
 }
 /** Integration hook: Task 6 can call this after creating an assignment in the Task 9 table. */
 public void notifyAssignment(int assignmentId){
  String email;
  String agentName;
  int orderId;
  String date,address;
  try(Connection c=DatabaseConnection.getConnection();PreparedStatement ps=c.prepareStatement(
    "SELECT a.email,a.name,d.order_id,d.scheduled_date,d.delivery_address FROM task9_delivery_assignment d JOIN delivery_agent a ON a.agent_id=d.agent_id WHERE d.assignment_id=?")){
   ps.setInt(1,assignmentId);
   try(ResultSet rs=ps.executeQuery()){
    if(!rs.next())throw new IllegalArgumentException("Assignment not found: "+assignmentId);
    email=rs.getString(1);agentName=rs.getString(2);orderId=rs.getInt(3);date=rs.getString(4);address=rs.getString(5);
   }
  }catch(SQLException e){throw new IllegalStateException("Cannot read assignment: "+e.getMessage(),e);}
  String subject="EcoNest: New delivery assignment #"+assignmentId;
  String body="Hello "+agentName+",\n\nYou have been assigned a new delivery.\nOrder: #"+orderId+"\nScheduled date: "+date+"\nDelivery address: "+address+"\n\nEcoNest";
  int id=notifications.save(assignmentId,email,subject,body);
  try{
   if(email==null||!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$"))throw new IllegalArgumentException("Invalid agent email address.");
   send(email,subject,body);
   notifications.markSent(id);
  }catch(Exception e){
   notifications.markFailed(id,e.getMessage()==null?e.getClass().getSimpleName():e.getMessage());
   throw new IllegalStateException("Delivery assigned (#"+assignmentId+") but notification FAILED: "+e.getMessage(),e);
  }
 }
 private void send(String to,String subject,String body) throws MessagingException {
  EmailConfig cfg=EmailConfig.fromEnvironment();
  Properties props=new Properties();props.put("mail.smtp.auth","true");props.put("mail.smtp.starttls.enable",Boolean.toString(cfg.startTls()));props.put("mail.smtp.host",cfg.host());props.put("mail.smtp.port",Integer.toString(cfg.port()));
  Session session=Session.getInstance(props,new Authenticator(){@Override protected PasswordAuthentication getPasswordAuthentication(){return new PasswordAuthentication(cfg.username(),cfg.password());}});
  MimeMessage msg=new MimeMessage(session);msg.setFrom(new InternetAddress(cfg.from()));msg.setRecipients(Message.RecipientType.TO,InternetAddress.parse(to,true));msg.setSubject(subject);msg.setText(body);Transport.send(msg);
 }
}
