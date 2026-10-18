package com.econest;

import com.econest.controller.ClientController;
import com.econest.controller.ProductController;
import com.econest.model.dao.ClientDAOImpl;
import com.econest.model.dao.DatabaseConnection;
import com.econest.model.dao.ProductDAOImpl;
import com.econest.model.service.ClientService;
import com.econest.model.service.ProductService;
import com.econest.view.ClientPanel;
import com.econest.view.ProductPanel;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Application entry point. Each team member adds their own tab here
 */
public class Main {

    public static void main(String[] args) {
        DatabaseConnection.initialize();

        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();   // dark theme must run before anything cause eyes are getting cooked

            JTabbedPane tabs = new JTabbedPane();

            ProductPanel productPanel = new ProductPanel();
            new ProductController(productPanel, new ProductService(new ProductDAOImpl()));
            tabs.addTab("Products", productPanel);

            ClientPanel clientPanel = new ClientPanel();
            new ClientController(clientPanel, new ClientService(new ClientDAOImpl()));
            tabs.addTab("Clients", clientPanel);

            // TODO (teammates): tabs.addTab("Inventory", and ohers like here), "Orders", "Delivery", "Reports"

            JFrame frame = new JFrame("EcoNest");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(tabs);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
