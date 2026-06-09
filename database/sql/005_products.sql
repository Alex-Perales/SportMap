-- ===================================
-- 005_products.sql
-- Tabla de Productos de la Tienda
-- ===================================

CREATE TABLE IF NOT EXISTS products (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    description TEXT,
    price REAL NOT NULL,
    image_url TEXT,
    images_urls TEXT,
    category TEXT,
    sizes TEXT,
    colors TEXT,
    stock INTEGER DEFAULT 0,
    is_on_sale BOOLEAN DEFAULT 0,
    discount_percent INTEGER DEFAULT 0,
    rating REAL DEFAULT 0.0,
    total_reviews INTEGER DEFAULT 0,
    vendor_id INTEGER,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY(vendor_id) REFERENCES users(id)
);

-- Índices para búsquedas de productos
CREATE INDEX IF NOT EXISTS idx_products_category ON products(category);
CREATE INDEX IF NOT EXISTS idx_products_vendor_id ON products(vendor_id);
CREATE INDEX IF NOT EXISTS idx_products_is_on_sale ON products(is_on_sale);

-- Datos de ejemplo
INSERT OR IGNORE INTO products (name, description, price, image_url, category, sizes, stock, is_on_sale, discount_percent, vendor_id, created_at, updated_at)
VALUES 
    ('Zapatillas Running Nike', 'Zapatillas de última generación para correr', 350.0, 'url_image_1', 'calzado', '36,37,38,39,40,41,42', 50, 0, 0, 3, 1686700000000, 1686700000000),
    ('Balón de Fútbol Adidas', 'Balón oficial para competiciones', 120.0, 'url_image_2', 'balones', '', 30, 1, 20, 3, 1686710000000, 1686710000000),
    ('Botella de Hidratación', 'Botella térmica para entrenamientos', 45.0, 'url_image_3', 'hidratación', '', 100, 0, 0, 1, 1686720000000, 1686720000000);
