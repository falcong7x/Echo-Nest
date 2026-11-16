CREATE TABLE IF NOT EXISTS client_order (
    order_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    client_id     INTEGER NOT NULL,
    order_date    TEXT NOT NULL,
    status        TEXT NOT NULL CHECK (status IN ('Pending', 'Confirmed', 'Cancelled', 'Completed')),
    total         REAL NOT NULL CHECK (total >= 0),
    FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS order_item (
    order_item_id INTEGER PRIMARY KEY AUTOINCREMENT,
    order_id      INTEGER NOT NULL,
    product_id    INTEGER NOT NULL,
    quantity      INTEGER NOT NULL CHECK (quantity > 0),
    unit_price    REAL NOT NULL CHECK (unit_price > 0),
    subtotal      REAL NOT NULL CHECK (subtotal > 0),
    FOREIGN KEY (order_id) REFERENCES client_order(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE RESTRICT,
    UNIQUE(order_id, product_id)
);
