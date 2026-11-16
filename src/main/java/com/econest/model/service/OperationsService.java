package com.econest.model.service;

import com.econest.model.dao.OperationsDAO;
import com.econest.model.operations.*;
import java.util.List;

public class OperationsService {
    private final OperationsDAO dao; private final EmailService emailService;
    public OperationsService(OperationsDAO dao, EmailService emailService){this.dao=dao;this.emailService=emailService;}
    public List<InventoryItem> inventory(){return dao.getInventory();}
    public void updateStock(int productId,int quantity,int reorderLevel){dao.updateStock(productId,quantity,reorderLevel);}
    public int createOrder(int clientId,int productId,int quantity){return dao.createOrder(clientId,productId,quantity);}
    public List<OrderSummary> orders(){return dao.getOrders();}
    public List<MonthlyReport> monthlyReports(){return dao.getMonthlyReports();}
    public void dispatch(int orderId){dao.markDispatched(orderId);OrderSummary order=dao.getOrder(orderId);emailService.sendDispatchNotification(order);}
}
