-- ===================================
-- 002_places.sql
-- Tabla de Lugares Deportivos
-- ===================================

CREATE TABLE IF NOT EXISTS places (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    sport_type TEXT,
    category TEXT,
    lat REAL NOT NULL,
    lng REAL NOT NULL,
    address TEXT,
    is_private BOOLEAN DEFAULT 0,
    description TEXT,
    services TEXT,
    photo_urls TEXT,
    rating REAL DEFAULT 4.5,
    price_per_hour REAL DEFAULT 0.0,
    air_quality_index INTEGER DEFAULT 50,
    owner_id INTEGER,
    total_reviews INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(owner_id) REFERENCES users(id)
);

-- Índices para búsquedas espaciales y filtros
CREATE INDEX IF NOT EXISTS idx_places_sport_type ON places(sport_type);
CREATE INDEX IF NOT EXISTS idx_places_category ON places(category);
CREATE INDEX IF NOT EXISTS idx_places_lat_lng ON places(lat, lng);
CREATE INDEX IF NOT EXISTS idx_places_owner_id ON places(owner_id);

-- Datos de ejemplo
INSERT OR IGNORE INTO places (name, sport_type, category, lat, lng, address, description, services, rating, price_per_hour, owner_id, created_at, updated_at)
VALUES 
    ('Complejo Deportivo Miraflores', 'futbol', 'field', -12.1181, -77.0302, 'Av. Principal 123', 'Canchas de fútbol de alta calidad', 'seguridad,baños,iluminación', 4.8, 50.0, 1, 1686700000000, 1686700000000),
    ('Pista de Running Lima', 'running', 'trayecto', -12.0975, -77.0250, 'Parque Central', 'Ruta para correr de 5km', 'seguridad,bebederos,iluminación', 4.6, 0.0, 2, 1686710000000, 1686710000000),
    ('Piscina Olímpica', 'natacion', 'field', -12.0800, -77.0300, 'Av. Deportes 456', 'Piscina olímpica temperatura controlada', 'seguridad,baños,vestuarios', 4.9, 30.0, 3, 1686720000000, 1686720000000);
