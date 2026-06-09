-- ===================================
-- 003_activities.sql
-- Tabla de Actividades Registradas
-- ===================================

CREATE TABLE IF NOT EXISTS activities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL,
    distance_km REAL,
    duration_minutes INTEGER,
    calories_burned INTEGER,
    place_id INTEGER,
    route_coordinates TEXT,
    date INTEGER NOT NULL,
    created_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(place_id) REFERENCES places(id)
);

-- Índices para búsquedas de actividades
CREATE INDEX IF NOT EXISTS idx_activities_user_id ON activities(user_id);
CREATE INDEX IF NOT EXISTS idx_activities_date ON activities(date);
CREATE INDEX IF NOT EXISTS idx_activities_type ON activities(type);
CREATE INDEX IF NOT EXISTS idx_activities_place_id ON activities(place_id);

-- Datos de ejemplo
INSERT OR IGNORE INTO activities (user_id, type, distance_km, duration_minutes, calories_burned, place_id, date, created_at)
VALUES 
    (1, 'running', 5.2, 35, 450, 2, 1686700000000, 1686700000000),
    (1, 'cycling', 15.0, 60, 600, NULL, 1686710000000, 1686710000000),
    (2, 'futbol', 0.0, 90, 550, 1, 1686720000000, 1686720000000);
