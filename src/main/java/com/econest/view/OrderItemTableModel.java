package com.econest.view;
import com.econest.model.OrderItem;import javax.swing.table.AbstractTableModel;import java.util.*;
public class OrderItemTableModel extends AbstractTableModel{
 private static final String[] C={"Product ID","Product","Qty","Unit Price","Subtotal"};private final List<OrderItem> data=new ArrayList<>();
 public void setItems(List<OrderItem>x){data.clear();if(x!=null)data.addAll(x);fireTableDataChanged();}public List<OrderItem> getItems(){return new ArrayList<>(data);}public void add(OrderItem i){data.add(i);fireTableDataChanged();}public void remove(int r){data.remove(r);fireTableDataChanged();}public int getRowCount(){return data.size();}public int getColumnCount(){return C.length;}public String getColumnName(int c){return C[c];}
 public Object getValueAt(int r,int c){OrderItem i=data.get(r);return switch(c){case 0->i.getProductId();case 1->i.getProductName();case 2->i.getQuantity();case 3->String.format("%.2f",i.getUnitPrice());default->String.format("%.2f",i.getSubtotal());};}
}
