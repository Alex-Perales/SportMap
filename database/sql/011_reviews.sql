-- ===================================
-- 011_reviews.sql
-- Tabla de Reseñas de Lugares
-- ===================================

CREATE TABLE IF NOT EXISTS reviews (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    place_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    rating INTEGER NOT NULL,
    title TEXT,
    comment TEXT,
    photo_urls TEXT,
    helpful_count INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(place_id) REFERENCES places(id),
    FOREIGN KEY(user_id) REFERENCES users(id)
);

-- Índices para búsquedas
CREATE INDEX IF NOT EXISTS idx_reviews_place_id ON reviews(place_id);
CREATE INDEX IF NOT EXISTS idx_reviews_user_id ON reviews(user_id);
CREATE INDEX IF NOT EXISTS idx_reviews_rating ON reviews(rating);

-- Datos de ejemplo
INSERT OR IGNORE INTO reviews (place_id, user_id, rating, title, comment, created_at, updated_at)
VALUES 
    (1, 1, 5, 'Excelente complejo', 'Las canchas están en perfecto estado, personal muy atento', 1686700000000, 1686700000000),
    (2, 2, 4, 'Buena ruta', 'La ruta es segura, aunque necesita mejor iluminación nocturna', 1686710000000, 1686710000000);
