CREATE TABLE IF NOT EXISTS product (
    product_id         INTEGER PRIMARY KEY AUTOINCREMENT,
    name               TEXT    NOT NULL UNIQUE,
    category           TEXT    NOT NULL CHECK (category IN ('Plant', 'Planter', 'Gardening Kit')),
    price              REAL    NOT NULL CHECK (price > 0),
    eco_rating         INTEGER NOT NULL CHECK (eco_rating BETWEEN 1 AND 5),
    species            TEXT,
    sunlight_needs     TEXT,
    watering_frequency TEXT,
    material           TEXT,
    size               TEXT,
    kit_contents       TEXT,
    bundle_discount    REAL
);
