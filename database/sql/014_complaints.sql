-- ===================================
-- 014_complaints.sql
-- Tabla de Quejas Formales
-- ===================================

CREATE TABLE IF NOT EXISTS complaints (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    complaint_against_user_id INTEGER,
    complaint_against_place_id INTEGER,
    category TEXT NOT NULL,
    title TEXT,
    description TEXT,
    severity TEXT DEFAULT 'low',
    status TEXT DEFAULT 'submitted',
    attachment_urls TEXT,
    resolution_notes TEXT,
    created_at INTEGER NOT NULL,
    resolved_at INTEGER,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(complaint_against_user_id) REFERENCES users(id),
    FOREIGN KEY(complaint_against_place_id) REFERENCES places(id)
);

-- Índices para búsquedas
CREATE INDEX IF NOT EXISTS idx_complaints_user_id ON complaints(user_id);
CREATE INDEX IF NOT EXISTS idx_complaints_status ON complaints(status);
CREATE INDEX IF NOT EXISTS idx_complaints_severity ON complaints(severity);

-- Datos de ejemplo
INSERT OR IGNORE INTO complaints (user_id, complaint_against_user_id, category, title, severity, status, created_at)
VALUES 
    (2, 1, 'bad_behavior', 'Usuario grosero durante la reserva', 'medium', 'submitted', 1686700000000);
