-- ===================================
-- 007_medals.sql
-- Tabla de Medallas y Logros
-- ===================================

CREATE TABLE IF NOT EXISTS medals (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    description TEXT,
    icon_key TEXT,
    condition TEXT,
    earned BOOLEAN DEFAULT 0,
    earned_date INTEGER,
    tier TEXT DEFAULT 'bronze',
    points INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

-- Índices para búsquedas
CREATE INDEX IF NOT EXISTS idx_medals_user_id ON medals(user_id);
CREATE INDEX IF NOT EXISTS idx_medals_earned ON medals(earned);

-- Datos de ejemplo
INSERT OR IGNORE INTO medals (user_id, name, description, icon_key, tier, earned, earned_date, points, created_at)
VALUES 
    (1, 'Corredor Madrugador', 'Completa 5 carreras antes de las 7am', 'ic_early_bird', 'bronze', 1, 1686700000000, 50, 1686700000000),
    (1, 'Ciclista de Hierro', 'Recorre 100km en un mes', 'ic_cyclist', 'silver', 1, 1686710000000, 100, 1686700000000),
    (2, 'Futbolista Dedicado', 'Participa en 10 partidos', 'ic_football', 'bronze', 0, NULL, 75, 1686700000000);
