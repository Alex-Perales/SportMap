-- ===================================
-- 001_users.sql
-- Tabla de Usuarios
-- ===================================

CREATE TABLE IF NOT EXISTS users (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    email TEXT UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    phone TEXT,
    district TEXT,
    bio TEXT,
    profile_image_url TEXT,
    is_premium BOOLEAN DEFAULT 0,
    kyc_status TEXT DEFAULT 'pending',
    rating REAL DEFAULT 0.0,
    total_activities INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

-- Índices para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_kyc_status ON users(kyc_status);

-- Datos de ejemplo
INSERT OR IGNORE INTO users (name, email, password_hash, phone, district, bio, is_premium, kyc_status, created_at, updated_at) 
VALUES 
    ('Juan García', 'juan@example.com', 'hash_123', '987654321', 'Miraflores', 'Corredor apasionado', 1, 'approved', 1686700000000, 1686700000000),
    ('María López', 'maria@example.com', 'hash_456', '987654322', 'San Isidro', 'Futbolista amateur', 0, 'pending', 1686710000000, 1686710000000),
    ('Carlos Ruiz', 'carlos@example.com', 'hash_789', '987654323', 'Barranco', 'Ciclista profesional', 1, 'approved', 1686720000000, 1686720000000);
