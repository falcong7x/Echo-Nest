package com.econest.model.service;

import com.econest.model.operations.OrderSummary;
import com.econest.model.util.EmailConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    public void sendDispatchNotification(OrderSummary order) {
        EmailConfig cfg=EmailConfig.fromEnvironment();
        Properties props=new Properties();
        props.put("mail.smtp.auth","true"); props.put("mail.smtp.starttls.enable",Boolean.toString(cfg.startTls()));
        props.put("mail.smtp.host",cfg.host()); props.put("mail.smtp.port",Integer.toString(cfg.port()));
        Session session=Session.getInstance(props,new Authenticator(){@Override protected PasswordAuthentication getPasswordAuthentication(){return new PasswordAuthentication(cfg.username(),cfg.password());}});
        try {
            Message message=new MimeMessage(session); message.setFrom(new InternetAddress(cfg.from()));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(order.email()));
            message.setSubject("Your EcoNest order has been dispatched");
            message.setText("Dear "+order.clientName()+",\n\nYour EcoNest order #"+order.orderId()+" has been dispatched.\nOrder total: "+String.format("%.2f",order.total())+"\n\nThank you for choosing EcoNest.");
            Transport.send(message);
        } catch(MessagingException e){throw new IllegalStateException("Email could not be sent: "+e.getMessage(),e);}
    }
}
