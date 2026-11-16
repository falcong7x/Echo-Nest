package com.econest.view;

import com.econest.model.Client;
import com.econest.model.Product;
import com.econest.model.operations.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/** View for stock maintenance, order dispatch and monthly revenue reports. */
public class OperationsPanel extends JPanel {
    private final JComboBox<Client> clients=new JComboBox<>();
    private final JComboBox<Product> orderProducts=new JComboBox<>(), stockProducts=new JComboBox<>();
    private final JSpinner orderQty=new JSpinner(new SpinnerNumberModel(1,1,10000,1));
    private final JSpinner stockQty=new JSpinner(new SpinnerNumberModel(0,0,100000,1));
    private final JSpinner reorder=new JSpinner(new SpinnerNumberModel(5,0,100000,1));
    private final JTable inventoryTable=table(new String[]{"ID","Product","Category","Quantity","Reorder level","Status"});
    private final JTable orderTable=table(new String[]{"Order ID","Client","Email","Date","Status","Total"});
    private final JTable reportTable=table(new String[]{"Month","Dispatched orders","Revenue"});
    private final JLabel lowStockAlertLabel = new JLabel("Low-stock alerts: loading...");
    private final JLabel reportSummaryLabel = new JLabel("Monthly revenue summary");
    private Runnable refresh=()->{}, updateStock=()->{}, createOrder=()->{}, dispatch=()->{};
    public OperationsPanel(List<Client> clientList,List<Product> productList){setLayout(new BorderLayout(8,8));
        setClients(clientList); setProducts(productList);
        JTabbedPane tabs=new JTabbedPane();tabs.addTab("Inventory",inventoryTab());tabs.addTab("Orders & Dispatch",ordersTab());tabs.addTab("Monthly Reports",reportsTab());add(tabs,BorderLayout.CENTER);
    }
    private JPanel inventoryTab(){JPanel p=new JPanel(new BorderLayout(8,8));
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT));controls.add(new JLabel("Product"));controls.add(stockProducts);controls.add(new JLabel("Stock quantity"));controls.add(stockQty);controls.add(new JLabel("Low-stock level"));controls.add(reorder);
        JButton save=new JButton("Update stock");save.addActionListener(e->updateStock.run());JButton reload=new JButton("Refresh");reload.addActionListener(e->refresh.run());controls.add(save);controls.add(reload);
        JPanel inventoryTop = new JPanel(new BorderLayout(4,4));
        inventoryTop.add(controls, BorderLayout.NORTH);
        lowStockAlertLabel.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
        inventoryTop.add(lowStockAlertLabel, BorderLayout.SOUTH);
        p.add(inventoryTop,BorderLayout.NORTH);
        p.add(new JScrollPane(inventoryTable),BorderLayout.CENTER);return p;}
    private JPanel ordersTab(){JPanel p=new JPanel(new BorderLayout(8,8));p.add(new JScrollPane(orderTable),BorderLayout.CENTER);
        JPanel controls=new JPanel(new FlowLayout(FlowLayout.LEFT));controls.add(new JLabel("Client"));controls.add(clients);controls.add(new JLabel("Product"));controls.add(orderProducts);controls.add(new JLabel("Quantity"));controls.add(orderQty);
        JButton create=new JButton("Create order");create.addActionListener(e->createOrder.run());JButton sent=new JButton("Mark selected order dispatched + email");sent.addActionListener(e->dispatch.run());JButton reload=new JButton("Refresh");reload.addActionListener(e->refresh.run());controls.add(create);controls.add(sent);controls.add(reload);p.add(controls,BorderLayout.SOUTH);return p;}
    private JPanel reportsTab(){JPanel p=new JPanel(new BorderLayout(8,8));p.add(new JScrollPane(reportTable),BorderLayout.CENTER);JPanel bottom=new JPanel(new BorderLayout(8,8));reportSummaryLabel.setBorder(BorderFactory.createEmptyBorder(6,8,6,8));bottom.add(reportSummaryLabel,BorderLayout.NORTH);JButton reload=new JButton("Generate / Refresh monthly report");reload.addActionListener(e->refresh.run());bottom.add(reload,BorderLayout.SOUTH);p.add(bottom,BorderLayout.SOUTH);return p;}
    private static JTable table(String[] cols){return new JTable(new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}});}
    private void setRows(JTable t,Object[][] rows){DefaultTableModel m=(DefaultTableModel)t.getModel();m.setRowCount(0);for(Object[] row:rows)m.addRow(row);}
    public void setClients(List<Client> clientList){clients.removeAllItems();clientList.forEach(cl->clients.addItem(cl));}
    public void setProducts(List<Product> productList){orderProducts.removeAllItems();stockProducts.removeAllItems();productList.forEach(p->{orderProducts.addItem(p);stockProducts.addItem(p);});}
    public void showInventory(List<InventoryItem> items){Object[][] rows=new Object[items.size()][6];java.util.List<String> lowNames=new java.util.ArrayList<>();for(int i=0;i<items.size();i++){InventoryItem x=items.get(i);rows[i]=new Object[]{x.productId(),x.productName(),x.category(),x.quantity(),x.reorderLevel(),x.isLowStock()?"LOW STOCK":"OK"};if(x.isLowStock())lowNames.add(x.productName()+" ("+x.quantity()+" left)");}setRows(inventoryTable,rows);if(lowNames.isEmpty()){lowStockAlertLabel.setText("Low-stock alerts: No products need restocking.");lowStockAlertLabel.setForeground(new Color(46,160,90));}else{lowStockAlertLabel.setText("LOW-STOCK ALERT ("+lowNames.size()+"): "+String.join(", ",lowNames));lowStockAlertLabel.setForeground(new Color(220,80,65));}}
    public void showOrders(List<OrderSummary> items){Object[][] rows=new Object[items.size()][6];for(int i=0;i<items.size();i++){OrderSummary x=items.get(i);rows[i]=new Object[]{x.orderId(),x.clientName(),x.email(),x.orderDate(),x.status(),String.format("%.2f",x.total())};}setRows(orderTable,rows);}
    public void showReports(List<MonthlyReport> items){Object[][] rows=new Object[items.size()][3];double totalRevenue=0;int totalOrders=0;for(int i=0;i<items.size();i++){MonthlyReport x=items.get(i);rows[i]=new Object[]{x.month(),x.dispatchedOrders(),String.format("%.2f",x.revenue())};totalRevenue+=x.revenue();totalOrders+=x.dispatchedOrders();}setRows(reportTable,rows);reportSummaryLabel.setText("Overall summary: "+totalOrders+" dispatched orders | Total revenue: "+String.format("%.2f",totalRevenue));}
    public int selectedStockProductId(){Product p=(Product)stockProducts.getSelectedItem();if(p==null)throw new IllegalArgumentException("Add a product first.");return p.getId();}
    public int selectedOrderProductId(){Product p=(Product)orderProducts.getSelectedItem();if(p==null)throw new IllegalArgumentException("Add a product first.");return p.getId();}
    public int selectedClientId(){Client c=(Client)clients.getSelectedItem();if(c==null)throw new IllegalArgumentException("Add a client first.");return c.getId();}
    public int orderQuantity(){return (Integer)orderQty.getValue();} public int stockQuantity(){return (Integer)stockQty.getValue();} public int reorderLevel(){return (Integer)reorder.getValue();}
    public int selectedOrderId(){int row=orderTable.getSelectedRow();if(row<0)throw new IllegalArgumentException("Select an order first.");return (Integer)orderTable.getValueAt(row,0);}
    public void onRefresh(Runnable r){refresh=r;}public void onUpdateStock(Runnable r){updateStock=r;}public void onCreateOrder(Runnable r){createOrder=r;}public void onDispatch(Runnable r){dispatch=r;}
    public void showMessage(String msg){JOptionPane.showMessageDialog(this,msg);}public void showError(String msg){JOptionPane.showMessageDialog(this,msg,"EcoNest",JOptionPane.ERROR_MESSAGE);}
}
