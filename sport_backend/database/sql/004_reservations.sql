-- ===================================
-- 004_reservations.sql
-- Tabla de Reservaciones
-- ===================================

CREATE TABLE IF NOT EXISTS reservations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    place_id INTEGER NOT NULL,
    place_name TEXT,
    reservation_date INTEGER NOT NULL,
    start_time TEXT,
    end_time TEXT,
    people_count INTEGER,
    total_price REAL,
    status TEXT DEFAULT 'confirmed',
    cancellation_reason TEXT,
    qr_code TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(place_id) REFERENCES places(id)
);

-- Índices para búsquedas de reservas
CREATE INDEX IF NOT EXISTS idx_reservations_user_id ON reservations(user_id);
CREATE INDEX IF NOT EXISTS idx_reservations_place_id ON reservations(place_id);
CREATE INDEX IF NOT EXISTS idx_reservations_status ON reservations(status);
CREATE INDEX IF NOT EXISTS idx_reservations_date ON reservations(reservation_date);

-- Datos de ejemplo
INSERT OR IGNORE INTO reservations (user_id, place_id, place_name, reservation_date, start_time, end_time, people_count, total_price, status, created_at, updated_at)
VALUES 
    (1, 1, 'Complejo Deportivo Miraflores', 1686804000000, '18:00', '19:30', 10, 150.0, 'confirmed', 1686700000000, 1686700000000),
    (2, 3, 'Piscina Olímpica', 1686890000000, '14:00', '15:00', 1, 30.0, 'confirmed', 1686710000000, 1686710000000);
