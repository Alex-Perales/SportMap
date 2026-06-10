-- ===================================
-- 008_user_kyc.sql
-- Tabla de Verificación de Identidad (KYC)
-- ===================================

CREATE TABLE IF NOT EXISTS user_kyc (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER UNIQUE NOT NULL,
    document_type TEXT,
    document_number TEXT,
    document_front_url TEXT,
    document_back_url TEXT,
    selfie_url TEXT,
    full_name TEXT,
    date_of_birth INTEGER,
    address TEXT,
    city TEXT,
    country TEXT,
    status TEXT DEFAULT 'pending',
    rejection_reason TEXT,
    verified_at INTEGER,
    expires_at INTEGER,
    created_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

-- Índices para búsquedas
CREATE INDEX IF NOT EXISTS idx_user_kyc_user_id ON user_kyc(user_id);
CREATE INDEX IF NOT EXISTS idx_user_kyc_status ON user_kyc(status);

-- Datos de ejemplo
INSERT OR IGNORE INTO user_kyc (user_id, document_type, document_number, full_name, status, verified_at, created_at)
VALUES 
    (1, 'DNI', '12345678', 'Juan García García', 'approved', 1686700000000, 1686700000000),
    (2, 'DNI', '87654321', 'María López Ruiz', 'pending', NULL, 1686710000000);
