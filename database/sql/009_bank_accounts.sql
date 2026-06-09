-- ===================================
-- 009_bank_accounts.sql
-- Tabla de Cuentas Bancarias
-- ===================================

CREATE TABLE IF NOT EXISTS bank_accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    account_type TEXT,
    bank_name TEXT,
    account_number TEXT,
    routing_number TEXT,
    account_holder_name TEXT,
    currency TEXT DEFAULT 'PEN',
    is_default BOOLEAN DEFAULT 0,
    is_active BOOLEAN DEFAULT 1,
    verification_status TEXT DEFAULT 'pending',
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id)
);

-- Índices para búsquedas
CREATE INDEX IF NOT EXISTS idx_bank_accounts_user_id ON bank_accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_bank_accounts_is_default ON bank_accounts(is_default);

-- Datos de ejemplo
INSERT OR IGNORE INTO bank_accounts (user_id, account_type, bank_name, account_holder_name, currency, is_default, verification_status, created_at, updated_at)
VALUES 
    (1, 'checking', 'BCP', 'Juan García García', 'PEN', 1, 'verified', 1686700000000, 1686700000000),
    (1, 'wallet', 'SportMap Wallet', 'Juan García García', 'PEN', 0, 'verified', 1686710000000, 1686710000000);
