-- Task 9 standalone assignment records until Task 6's module is integrated.
CREATE TABLE IF NOT EXISTS task9_delivery_assignment (
 assignment_id INTEGER PRIMARY KEY AUTOINCREMENT,
 order_id INTEGER NOT NULL REFERENCES client_order(order_id),
 agent_id INTEGER NOT NULL REFERENCES delivery_agent(agent_id),
 scheduled_date TEXT NOT NULL,
 delivery_address TEXT NOT NULL,
 created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE IF NOT EXISTS notification (
 notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
 assignment_id INTEGER NOT NULL REFERENCES task9_delivery_assignment(assignment_id),
 recipient_email TEXT NOT NULL,
 subject TEXT NOT NULL,
 message TEXT NOT NULL,
 status TEXT NOT NULL DEFAULT 'PENDING' CHECK(status IN ('PENDING','SENT','FAILED')),
 created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
 sent_at TEXT,
 error_message TEXT
);
