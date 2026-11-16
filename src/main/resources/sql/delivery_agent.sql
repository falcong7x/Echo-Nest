CREATE TABLE IF NOT EXISTS delivery_agent (
    agent_id       INTEGER PRIMARY KEY AUTOINCREMENT,
    name           TEXT NOT NULL,
    phone          TEXT NOT NULL,
    email          TEXT NOT NULL UNIQUE,
    address        TEXT NOT NULL,
    vehicle_type   TEXT NOT NULL,
    vehicle_number TEXT NOT NULL UNIQUE,
    status         TEXT NOT NULL CHECK (status IN ('Available', 'Unavailable'))
);
