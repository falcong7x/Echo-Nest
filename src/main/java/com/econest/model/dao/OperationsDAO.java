package com.econest.model.dao;

import com.econest.model.exception.DatabaseException;
import com.econest.model.operations.*;
import java.sql.*;
import java.util.*;

public class OperationsDAO {
    public List<InventoryItem> getInventory() {
        String sql = "SELECT p.product_id,p.name,p.category,i.quantity,i.reorder_level FROM product p JOIN inventory i ON p.product_id=i.product_id ORDER BY p.name";
        List<InventoryItem> out = new ArrayList<>();
        try (Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement(sql); ResultSet rs=ps.executeQuery()) {
            while(rs.next()) out.add(new InventoryItem(rs.getInt(1),rs.getString(2),rs.getString(3),rs.getInt(4),rs.getInt(5)));
            return out;
        } catch(SQLException e){ throw new DatabaseException("Could not load inventory: "+e.getMessage(),e); }
    }
    public void updateStock(int productId,int quantity,int reorderLevel) {
        if(quantity<0||reorderLevel<0) throw new IllegalArgumentException("Stock and reorder level cannot be negative.");
        try(Connection c=DatabaseConnection.getConnection(); PreparedStatement ps=c.prepareStatement("INSERT INTO inventory(product_id,quantity,reorder_level) VALUES(?,?,?) ON CONFLICT(product_id) DO UPDATE SET quantity=excluded.quantity,reorder_level=excluded.reorder_level")){
            ps.setInt(1,productId); ps.setInt(2,quantity); ps.setInt(3,reorderLevel); ps.executeUpdate();
        }catch(SQLException e){throw new DatabaseException("Could not update stock: "+e.getMessage(),e);}
    }
    public int createOrder(int clientId,int productId,int quantity) {
        if(quantity<=0) throw new IllegalArgumentException("Quantity must be greater than zero.");
        String stockSql="SELECT i.quantity,p.price FROM inventory i JOIN product p ON p.product_id=i.product_id WHERE i.product_id=?";
        try(Connection c=DatabaseConnection.getConnection()){
            c.setAutoCommit(false);
            try(PreparedStatement ps=c.prepareStatement(stockSql)){
                ps.setInt(1,productId); try(ResultSet rs=ps.executeQuery()){
                    if(!rs.next()) throw new IllegalArgumentException("Product or inventory record not found.");
                    if(rs.getInt("quantity")<quantity) throw new IllegalArgumentException("Not enough stock available.");
                    double total=rs.getDouble("price")*quantity;
                    int orderId;
                    try(PreparedStatement op=c.prepareStatement("INSERT INTO customer_order(client_id,total) VALUES(?,?)",Statement.RETURN_GENERATED_KEYS)){
                        op.setInt(1,clientId);op.setDouble(2,total);op.executeUpdate();try(ResultSet keys=op.getGeneratedKeys()){if(!keys.next())throw new SQLException("Order ID was not generated.");orderId=keys.getInt(1);}}
                    try(PreparedStatement item=c.prepareStatement("INSERT INTO order_item(order_id,product_id,quantity,unit_price) VALUES(?,?,?,?)")){
                        item.setInt(1,orderId);item.setInt(2,productId);item.setInt(3,quantity);item.setDouble(4,total/quantity);item.executeUpdate();}
                    try(PreparedStatement upd=c.prepareStatement("UPDATE inventory SET quantity=quantity-? WHERE product_id=?")){upd.setInt(1,quantity);upd.setInt(2,productId);upd.executeUpdate();}
                    c.commit(); return orderId;
                }
            }catch(SQLException | RuntimeException e){c.rollback();throw e;}finally{c.setAutoCommit(true);}
        }catch(SQLException e){throw new DatabaseException("Could not create order: "+e.getMessage(),e);}
    }
    public List<OrderSummary> getOrders(){
        String sql="SELECT o.order_id,o.client_id,c.name,c.email,o.order_date,o.status,o.total FROM customer_order o JOIN client c ON c.client_id=o.client_id ORDER BY o.order_id DESC";
        List<OrderSummary> out=new ArrayList<>();try(Connection c=DatabaseConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql);ResultSet rs=ps.executeQuery()){
            while(rs.next())out.add(new OrderSummary(rs.getInt(1),rs.getInt(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getDouble(7)));return out;
        }catch(SQLException e){throw new DatabaseException("Could not load orders: "+e.getMessage(),e);}
    }
    public OrderSummary getOrder(int orderId){return getOrders().stream().filter(o->o.orderId()==orderId).findFirst().orElseThrow(()->new IllegalArgumentException("Order not found."));}
    public void markDispatched(int orderId){
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement ps=c.prepareStatement("UPDATE customer_order SET status='Dispatched',dispatched_at=datetime('now','localtime') WHERE order_id=? AND status='Pending'")){
            ps.setInt(1,orderId);if(ps.executeUpdate()==0)throw new IllegalArgumentException("Order not found or already dispatched.");
        }catch(SQLException e){throw new DatabaseException("Could not dispatch order: "+e.getMessage(),e);}
    }
    public List<MonthlyReport> getMonthlyReports(){
        String sql="SELECT strftime('%Y-%m',order_date) month,COUNT(*) orders,COALESCE(SUM(total),0) revenue FROM customer_order WHERE status='Dispatched' GROUP BY strftime('%Y-%m',order_date) ORDER BY month DESC";
        List<MonthlyReport> out=new ArrayList<>();try(Connection c=DatabaseConnection.getConnection();PreparedStatement ps=c.prepareStatement(sql);ResultSet rs=ps.executeQuery()){
            while(rs.next())out.add(new MonthlyReport(rs.getString(1),rs.getInt(2),rs.getDouble(3)));return out;
        }catch(SQLException e){throw new DatabaseException("Could not generate monthly reports: "+e.getMessage(),e);}
    }
}
