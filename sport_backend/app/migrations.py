"""Crea todas las tablas si no existen. Se ejecuta al arrancar la API."""

CREATE_TABLES_SQL = """
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    district VARCHAR(100),
    bio TEXT,
    profile_image_url VARCHAR(500),
    is_premium BOOLEAN DEFAULT false,
    is_admin BOOLEAN DEFAULT false,
    kyc_status VARCHAR(50) DEFAULT 'pending',
    rating DECIMAL(3,2) DEFAULT 0.0,
    total_activities INTEGER DEFAULT 0,
    fcm_token VARCHAR(500),
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS is_admin BOOLEAN DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS fcm_token VARCHAR(500);

CREATE TABLE IF NOT EXISTS places (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    sport_type VARCHAR(100),
    category VARCHAR(50),
    lat DECIMAL(10, 8) NOT NULL,
    lng DECIMAL(11, 8) NOT NULL,
    address VARCHAR(500),
    is_private BOOLEAN DEFAULT false,
    description TEXT,
    services TEXT,
    photo_urls TEXT,
    rating DECIMAL(3,2) DEFAULT 4.5,
    price_per_hour DECIMAL(8,2) DEFAULT 0.0,
    air_quality_index INTEGER DEFAULT 50,
    owner_id INTEGER,
    total_reviews INTEGER DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS activities (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    type VARCHAR(100) NOT NULL,
    distance_km DECIMAL(8,2),
    duration_minutes INTEGER,
    calories_burned INTEGER,
    place_id INTEGER,
    route_coordinates TEXT,
    date BIGINT NOT NULL,
    created_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    place_id INTEGER NOT NULL,
    place_name VARCHAR(255),
    reservation_date BIGINT NOT NULL,
    start_time VARCHAR(10),
    end_time VARCHAR(10),
    people_count INTEGER,
    total_price DECIMAL(8,2),
    status VARCHAR(50) DEFAULT 'confirmed',
    cancellation_reason TEXT,
    qr_code VARCHAR(500),
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(8,2) NOT NULL,
    image_url VARCHAR(500),
    images_urls TEXT,
    category VARCHAR(100),
    sizes TEXT,
    colors TEXT,
    stock INTEGER DEFAULT 0,
    is_on_sale BOOLEAN DEFAULT false,
    discount_percent INTEGER DEFAULT 0,
    rating DECIMAL(3,2) DEFAULT 0.0,
    total_reviews INTEGER DEFAULT 0,
    vendor_id INTEGER,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_items (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    product_name VARCHAR(255),
    product_image_url VARCHAR(500),
    unit_price DECIMAL(8,2),
    quantity INTEGER,
    selected_size VARCHAR(50),
    selected_color VARCHAR(50),
    added_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS medals (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon_key VARCHAR(100),
    condition TEXT,
    earned BOOLEAN DEFAULT false,
    earned_date BIGINT,
    tier VARCHAR(50) DEFAULT 'bronze',
    points INTEGER DEFAULT 0,
    created_at BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    type VARCHAR(100) NOT NULL,
    title VARCHAR(255),
    message TEXT,
    is_read BOOLEAN DEFAULT false,
    created_at BIGINT NOT NULL
);

-- Anuncios/banners de la sección "Recomendados para ti", administrables desde /admin/ads.
CREATE TABLE IF NOT EXISTS ads (
    id SERIAL PRIMARY KEY,
    image_url VARCHAR(500) NOT NULL,
    badge_text VARCHAR(50),
    title VARCHAR(255) NOT NULL,
    subtitle VARCHAR(255),
    price DECIMAL(8,2),
    link_type VARCHAR(20) NOT NULL DEFAULT 'none',  -- 'none' | 'premium' | 'product' | 'external'
    link_target VARCHAR(500),
    is_active BOOLEAN DEFAULT true,
    sort_order INTEGER DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

-- Pedidos pagados por Yape/Plin con comprobante, pendientes de revisión manual.
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    order_type VARCHAR(20) NOT NULL,      -- 'store' | 'reservation' | 'premium'
    reference_id INTEGER,                 -- reservation_id cuando order_type='reservation'
    amount DECIMAL(10,2) NOT NULL,
    items_json TEXT,                      -- snapshot del carrito cuando order_type='store'
    status VARCHAR(30) NOT NULL DEFAULT 'pendiente',  -- 'pendiente' | 'aprobado' | 'rechazado' | 'cancelado_reembolsado'
    proof_image_path VARCHAR(500) NOT NULL,
    admin_note TEXT,
    motivo_rechazo TEXT,
    created_at BIGINT NOT NULL,
    reviewed_at BIGINT
);

ALTER TABLE orders ADD COLUMN IF NOT EXISTS motivo_rechazo TEXT;
ALTER TABLE orders ALTER COLUMN status TYPE VARCHAR(30);
"""


async def run_migrations(conn):
    await conn.execute(CREATE_TABLES_SQL)
