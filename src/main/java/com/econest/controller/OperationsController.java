package com.econest.controller;

import com.econest.model.Client;
import com.econest.model.Product;
import com.econest.model.dao.ClientDAOImpl;
import com.econest.model.dao.ProductDAOImpl;
import com.econest.model.operations.*;
import com.econest.model.service.ClientService;
import com.econest.model.service.OperationsService;
import com.econest.model.service.ProductService;
import com.econest.view.OperationsPanel;
import javax.swing.*;

public class OperationsController {
    private final OperationsPanel view; private final OperationsService service;
    public OperationsController(OperationsPanel view, OperationsService service){this.view=view;this.service=service;
        view.onRefresh(this::refreshAll); view.onUpdateStock(this::updateStock); view.onCreateOrder(this::createOrder); view.onDispatch(this::dispatch);
        refreshAll();
    }
    public void refresh(){refreshAll();}
    private void refreshAll(){try{view.showInventory(service.inventory());view.showOrders(service.orders());view.showReports(service.monthlyReports());view.setClients(new ClientService(new ClientDAOImpl()).getAllClients());view.setProducts(new ProductService(new ProductDAOImpl()).getAllProducts());}catch(RuntimeException e){view.showError(e.getMessage());}}
    private void updateStock(){try{service.updateStock(view.selectedStockProductId(),view.stockQuantity(),view.reorderLevel());refreshAll();view.showMessage("Inventory updated.");}catch(RuntimeException e){view.showError(e.getMessage());}}
    private void createOrder(){try{int id=service.createOrder(view.selectedClientId(),view.selectedOrderProductId(),view.orderQuantity());refreshAll();view.showMessage("Order #"+id+" created successfully.");}catch(RuntimeException e){view.showError(e.getMessage());}}
    private void dispatch(){try{int id=view.selectedOrderId();service.dispatch(id);refreshAll();view.showMessage("Order dispatched and notification email sent.");}catch(RuntimeException e){view.showError(e.getMessage());}}
}
