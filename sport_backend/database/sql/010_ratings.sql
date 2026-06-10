-- ===================================
-- 010_ratings.sql
-- Tabla de Calificaciones entre Usuarios
-- ===================================

CREATE TABLE IF NOT EXISTS ratings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    from_user_id INTEGER NOT NULL,
    to_user_id INTEGER NOT NULL,
    reservation_id INTEGER,
    rating INTEGER NOT NULL,
    comment TEXT,
    category TEXT,
    created_at INTEGER NOT NULL,
    FOREIGN KEY(from_user_id) REFERENCES users(id),
    FOREIGN KEY(to_user_id) REFERENCES users(id),
    FOREIGN KEY(reservation_id) REFERENCES reservations(id)
);

-- Índices para búsquedas
CREATE INDEX IF NOT EXISTS idx_ratings_from_user ON ratings(from_user_id);
CREATE INDEX IF NOT EXISTS idx_ratings_to_user ON ratings(to_user_id);
CREATE INDEX IF NOT EXISTS idx_ratings_reservation ON ratings(reservation_id);

-- Datos de ejemplo
INSERT OR IGNORE INTO ratings (from_user_id, to_user_id, reservation_id, rating, comment, category, created_at)
VALUES 
    (1, 2, 1, 5, 'Excelente persona, muy puntual', 'communication', 1686700000000),
    (2, 1, 1, 4, 'Buena experiencia, recomendado', 'punctuality', 1686710000000);
