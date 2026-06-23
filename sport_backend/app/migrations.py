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
    kyc_status VARCHAR(50) DEFAULT 'pending',
    rating DECIMAL(3,2) DEFAULT 0.0,
    total_activities INTEGER DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

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
"""


async def run_migrations(conn):
    await conn.execute(CREATE_TABLES_SQL)
