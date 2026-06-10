-- ===================================
-- 015_notifications.sql
-- Tabla de Notificaciones
-- ===================================

CREATE TABLE IF NOT EXISTS notifications (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL,
    title TEXT,
    message TEXT,
    data TEXT,
    reference_type TEXT,
    reference_id INTEGER,
    is_read BOOLEAN DEFAULT 0,
    read_at INTEGER,
    created_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

-- Índices para búsquedas
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_is_read ON notifications(is_read);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);

-- Datos de ejemplo
INSERT OR IGNORE INTO notifications (user_id, type, title, message, is_read, created_at)
VALUES 
    (1, 'reservation_confirmed', 'Reserva confirmada', 'Tu reserva en Complejo Deportivo Miraflores fue confirmada', 1, 1686700000000),
    (2, 'rating_received', 'Nueva calificación', 'Juan García te ha calificado con 5 estrellas', 0, 1686710000000),
    (1, 'medal_earned', 'Logro conseguido', '¡Desbloqueaste la medalla Corredor Madrugador!', 0, 1686720000000);
