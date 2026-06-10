-- ===================================
-- 013_disputes.sql
-- Tabla de Disputas
-- ===================================

CREATE TABLE IF NOT EXISTS disputes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    reservation_id INTEGER,
    initiated_by_user_id INTEGER NOT NULL,
    reason TEXT,
    description TEXT,
    status TEXT DEFAULT 'open',
    resolution TEXT,
    evidence_urls TEXT,
    assigned_to_admin_id INTEGER,
    created_at INTEGER NOT NULL,
    resolved_at INTEGER,
    FOREIGN KEY(reservation_id) REFERENCES reservations(id),
    FOREIGN KEY(initiated_by_user_id) REFERENCES users(id),
    FOREIGN KEY(assigned_to_admin_id) REFERENCES users(id)
);

-- Índices para búsquedas
CREATE INDEX IF NOT EXISTS idx_disputes_reservation_id ON disputes(reservation_id);
CREATE INDEX IF NOT EXISTS idx_disputes_status ON disputes(status);
CREATE INDEX IF NOT EXISTS idx_disputes_created_at ON disputes(created_at);

-- Datos de ejemplo
INSERT OR IGNORE INTO disputes (reservation_id, initiated_by_user_id, reason, status, created_at)
VALUES 
    (1, 2, 'Cancha no disponible a la hora acordada', 'open', 1686700000000);
