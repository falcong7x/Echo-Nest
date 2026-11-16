package com.econest;

import com.econest.controller.ClientController;
import com.econest.controller.DeliveryController;
import com.econest.controller.ReportController;
import com.econest.model.dao.DeliveryDAO;
import com.econest.model.dao.ReportDAOImpl;
import com.econest.model.service.DeliveryService;
import com.econest.model.service.ReportService;
import com.econest.view.DeliveryPanel;
import com.econest.view.ReportPanel;
import com.econest.controller.ProductController;
import com.econest.model.dao.ClientDAOImpl;
import com.econest.model.dao.DatabaseConnection;
import com.econest.model.dao.ProductDAOImpl;
import com.econest.model.service.ClientService;
import com.econest.model.service.ProductService;
import com.econest.view.ClientPanel;
import com.econest.view.ProductPanel;
import com.econest.controller.OperationsController;
import com.econest.model.dao.OperationsDAO;
import com.econest.model.service.OperationsService;
import com.econest.model.service.EmailService;
import com.econest.view.OperationsPanel;
import com.econest.controller.DeliveryAgentController;
import com.econest.controller.OrderController;
import com.econest.model.dao.DeliveryAgentDAOImpl;
import com.econest.model.dao.OrderDAOImpl;
import com.econest.model.service.DeliveryAgentService;
import com.econest.model.service.OrderService;
import com.econest.view.DeliveryAgentPanel;
import com.econest.view.OrderPanel;
import com.econest.controller.NotificationController;
import com.econest.model.dao.NotificationDAOImpl;
import com.econest.model.service.AgentNotificationService;
import com.econest.view.NotificationPanel;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

/**
 * Application entry point. Each team member adds their own tab here
 * (create DAO -> service -> panel -> controller, then tabs.addTab(...)).
 */
public class Main {

    public static void main(String[] args) {
        DatabaseConnection.initialize();

        SwingUtilities.invokeLater(() -> {
            FlatDarkLaf.setup();   // dark theme, must run before any component is created

            JTabbedPane tabs = new JTabbedPane();

            ProductPanel productPanel = new ProductPanel();
            new ProductController(productPanel, new ProductService(new ProductDAOImpl()));
            tabs.addTab("Products", productPanel);

            ClientPanel clientPanel = new ClientPanel();
            new ClientController(clientPanel, new ClientService(new ClientDAOImpl()));
            tabs.addTab("Clients", clientPanel);

            DeliveryAgentPanel deliveryPanel = new DeliveryAgentPanel();
            new DeliveryAgentController(deliveryPanel, new DeliveryAgentService(new DeliveryAgentDAOImpl()));
            tabs.addTab("Delivery Agents", deliveryPanel);

            OrderPanel orderPanel = new OrderPanel();
            new OrderController(orderPanel,
                    new OrderService(new OrderDAOImpl(), new ClientService(new ClientDAOImpl()), new ProductService(new ProductDAOImpl())),
                    new ClientService(new ClientDAOImpl()), new ProductService(new ProductDAOImpl()));
            tabs.addTab("Orders", orderPanel);

            OperationsPanel operationsPanel = new OperationsPanel(
                    new ClientService(new ClientDAOImpl()).getAllClients(),
                    new ProductService(new ProductDAOImpl()).getAllProducts());
            new OperationsController(operationsPanel,
                    new OperationsService(new OperationsDAO(), new EmailService()));
            tabs.addTab("Orders, Inventory & Reports", operationsPanel);


            AgentNotificationService agentNotificationService = new AgentNotificationService(new NotificationDAOImpl());
            DeliveryPanel assignmentPanel = new DeliveryPanel();
            new DeliveryController(assignmentPanel, new DeliveryService(new DeliveryDAO()));
            tabs.addTab("Deliveries", assignmentPanel);

            ReportPanel reportPanel = new ReportPanel();
            ReportController reportController = new ReportController(reportPanel, new ReportService(new ReportDAOImpl()));
            tabs.addTab("Detailed Reports", reportPanel);

            NotificationPanel notificationPanel = new NotificationPanel();
            new NotificationController(notificationPanel, agentNotificationService);
            tabs.addTab("Agent Notifications", notificationPanel);

            JFrame frame = new JFrame("EcoNest");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(tabs);
            frame.setSize(1000, 650);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
