CREATE TABLE IF NOT EXISTS client (
    client_id      INTEGER PRIMARY KEY AUTOINCREMENT,
    name           TEXT NOT NULL,
    client_type    TEXT NOT NULL CHECK (client_type IN ('Household', 'Office')),
    email          TEXT NOT NULL UNIQUE,
    phone          TEXT NOT NULL,
    address        TEXT NOT NULL,
    household_size INTEGER,
    company_name   TEXT,
    contact_person TEXT
);
