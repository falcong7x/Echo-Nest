package com.econest.view;
import com.econest.model.DeliveryAgent;
import com.econest.model.Order;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import com.econest.model.Notification;
public class NotificationPanel extends JPanel {
 public final JComboBox<Order> orders=new JComboBox<>();
 public final JComboBox<DeliveryAgent> agents=new JComboBox<>();
 public final JTextField date=new JTextField(LocalDate.now().toString(),12);
 public final JTextField address=new JTextField(28);
 public final JButton assign=new JButton("Assign Delivery & Send Email");
 public final JButton refresh=new JButton("Refresh Lists / History");
 private final DefaultTableModel model=new DefaultTableModel(new Object[]{"ID","Assignment","Recipient","Status","Created","Sent","Error"},0){public boolean isCellEditable(int r,int c){return false;}};
 public NotificationPanel(){setLayout(new BorderLayout(10,10));
  JPanel top=new JPanel(new GridLayout(0,2,8,8));
  top.add(new JLabel("Order"));top.add(orders);top.add(new JLabel("Delivery agent"));top.add(agents);
  top.add(new JLabel("Delivery date (YYYY-MM-DD)"));top.add(date);top.add(new JLabel("Delivery address"));top.add(address);
  JPanel actions=new JPanel(new FlowLayout(FlowLayout.LEFT));actions.add(assign);actions.add(refresh);
  JPanel north=new JPanel(new BorderLayout());north.setBorder(BorderFactory.createEmptyBorder(12,12,0,12));north.add(top,BorderLayout.CENTER);north.add(actions,BorderLayout.SOUTH);
  add(north,BorderLayout.NORTH);
  JTable table=new JTable(model);table.setAutoCreateRowSorter(true);add(new JScrollPane(table),BorderLayout.CENTER);
  add(new JLabel("SMTP: set ECONEST_SMTP_HOST, ECONEST_SMTP_USER, ECONEST_SMTP_PASSWORD (optional PORT / EMAIL_FROM)."),BorderLayout.SOUTH);
 }
 public void showOptions(List<Order> orderList,List<DeliveryAgent> agentList){orders.removeAllItems();agents.removeAllItems();orderList.forEach(orders::addItem);agentList.forEach(agents::addItem);}
 public void showHistory(List<Notification> list){model.setRowCount(0);for(Notification n:list)model.addRow(new Object[]{n.id(),n.assignmentId(),n.recipientEmail(),n.status(),n.createdAt(),n.sentAt(),n.errorMessage()});}
 public void message(String s,int type){JOptionPane.showMessageDialog(this,s,"EcoNest",type);}
}
