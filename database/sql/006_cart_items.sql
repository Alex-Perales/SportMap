-- ===================================
-- 006_cart_items.sql
-- Tabla de Items en Carrito
-- ===================================

CREATE TABLE IF NOT EXISTS cart_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    product_name TEXT,
    product_image_url TEXT,
    unit_price REAL,
    quantity INTEGER,
    selected_size TEXT,
    selected_color TEXT,
    added_at INTEGER NOT NULL,
    FOREIGN KEY(user_id) REFERENCES users(id),
    FOREIGN KEY(product_id) REFERENCES products(id)
);

-- Índices para búsquedas rápidas
CREATE INDEX IF NOT EXISTS idx_cart_items_user_id ON cart_items(user_id);
CREATE INDEX IF NOT EXISTS idx_cart_items_product_id ON cart_items(product_id);

-- Datos de ejemplo
INSERT OR IGNORE INTO cart_items (user_id, product_id, product_name, product_image_url, unit_price, quantity, selected_size, added_at)
VALUES 
    (1, 1, 'Zapatillas Running Nike', 'url_image_1', 350.0, 1, '40', 1686700000000),
    (1, 3, 'Botella de Hidratación', 'url_image_3', 45.0, 2, '', 1686710000000);
