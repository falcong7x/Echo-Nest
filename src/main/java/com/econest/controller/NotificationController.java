package com.econest.controller;
import com.econest.model.DeliveryAgent;
import com.econest.model.Order;
import com.econest.model.dao.*;
import com.econest.model.service.AgentNotificationService;
import com.econest.view.NotificationPanel;
import javax.swing.*;
import java.time.LocalDate;
public class NotificationController {
 private final NotificationPanel view;
 private final AgentNotificationService service;
 private final OrderDAO orders=new OrderDAOImpl();
 private final DeliveryAgentDAO agents=new DeliveryAgentDAOImpl();
 public NotificationController(NotificationPanel view,AgentNotificationService service){this.view=view;this.service=service;
  view.refresh.addActionListener(e->refresh());view.assign.addActionListener(e->assign());refresh();
 }
 private void refresh(){try{view.showOptions(orders.findAll(),agents.findAll());view.showHistory(service.history());}
  catch(Exception e){view.message(e.getMessage(),JOptionPane.ERROR_MESSAGE);}}
 private void assign(){Order o=(Order)view.orders.getSelectedItem();DeliveryAgent a=(DeliveryAgent)view.agents.getSelectedItem();
  if(o==null||a==null){view.message("Create an order and delivery agent first.",JOptionPane.WARNING_MESSAGE);return;}
  final String dateText=view.date.getText().trim();
  final String addressText=view.address.getText();
  view.assign.setEnabled(false);
  new SwingWorker<Integer,Void>(){
   protected Integer doInBackground(){return service.assignAndNotify(o.getId(),a.getId(),LocalDate.parse(dateText),addressText);}
   protected void done(){view.assign.setEnabled(true);try{int id=get();view.message("Assignment #"+id+" created. Email submitted successfully.",JOptionPane.INFORMATION_MESSAGE);}
    catch(Exception e){Throwable cause=e.getCause()==null?e:e.getCause();view.message(cause.getMessage(),JOptionPane.WARNING_MESSAGE);}
    try{view.showHistory(service.history());}catch(Exception ignored){}
   }
  }.execute();
 }
}
