-- ===================================
-- 012_transactions.sql
-- Tabla de Transacciones
-- ===================================

CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    type TEXT NOT NULL,
    amount REAL NOT NULL,
    currency TEXT DEFAULT 'PEN',
    status TEXT DEFAULT 'pending',
    description TEXT,
    reference_id INTEGER,
    reference_type TEXT,
    payment_method TEXT,
    transaction_hash TEXT UNIQUE,
    created_at INTEGER NOT NULL,
    completed_at INTEGER,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

-- Índices para búsquedas
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_status ON transactions(status);
CREATE INDEX IF NOT EXISTS idx_transactions_type ON transactions(type);
CREATE INDEX IF NOT EXISTS idx_transactions_created_at ON transactions(created_at);

-- Datos de ejemplo
INSERT OR IGNORE INTO transactions (user_id, type, amount, currency, status, description, reference_type, payment_method, created_at, completed_at)
VALUES 
    (1, 'purchase', 350.0, 'PEN', 'completed', 'Compra de zapatillas', 'product', 'card', 1686700000000, 1686700000000),
    (1, 'reservation', 150.0, 'PEN', 'completed', 'Reserva cancha de fútbol', 'reservation', 'wallet', 1686710000000, 1686710000000);
