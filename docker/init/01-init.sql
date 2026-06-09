-- ===================================
-- INIT SCRIPT - Inicialización de BD
-- Ejecutado automáticamente por Docker
-- ===================================

-- Crear extensiones necesarias
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ===================================
-- TABLA: users
-- ===================================
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

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_kyc_status ON users(kyc_status);

-- ===================================
-- TABLA: places
-- ===================================
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
    owner_id INTEGER REFERENCES users(id),
    total_reviews INTEGER DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE INDEX idx_places_sport_type ON places(sport_type);
CREATE INDEX idx_places_category ON places(category);
CREATE INDEX idx_places_owner_id ON places(owner_id);

-- ===================================
-- TABLA: activities
-- ===================================
CREATE TABLE IF NOT EXISTS activities (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    distance_km DECIMAL(8,2),
    duration_minutes INTEGER,
    calories_burned INTEGER,
    place_id INTEGER REFERENCES places(id),
    route_coordinates TEXT,
    date BIGINT NOT NULL,
    created_at BIGINT NOT NULL
);

CREATE INDEX idx_activities_user_id ON activities(user_id);
CREATE INDEX idx_activities_date ON activities(date);
CREATE INDEX idx_activities_type ON activities(type);

-- ===================================
-- TABLA: reservations
-- ===================================
CREATE TABLE IF NOT EXISTS reservations (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    place_id INTEGER NOT NULL REFERENCES places(id) ON DELETE CASCADE,
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

CREATE INDEX idx_reservations_user_id ON reservations(user_id);
CREATE INDEX idx_reservations_place_id ON reservations(place_id);
CREATE INDEX idx_reservations_status ON reservations(status);

-- ===================================
-- TABLA: products
-- ===================================
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
    vendor_id INTEGER REFERENCES users(id),
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE INDEX idx_products_category ON products(category);
CREATE INDEX idx_products_vendor_id ON products(vendor_id);

-- ===================================
-- TABLA: cart_items
-- ===================================
CREATE TABLE IF NOT EXISTS cart_items (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    product_id INTEGER NOT NULL REFERENCES products(id),
    product_name VARCHAR(255),
    product_image_url VARCHAR(500),
    unit_price DECIMAL(8,2),
    quantity INTEGER,
    selected_size VARCHAR(50),
    selected_color VARCHAR(50),
    added_at BIGINT NOT NULL
);

CREATE INDEX idx_cart_items_user_id ON cart_items(user_id);

-- ===================================
-- TABLA: medals
-- ===================================
CREATE TABLE IF NOT EXISTS medals (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
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

CREATE INDEX idx_medals_user_id ON medals(user_id);

-- ===================================
-- TABLA: user_kyc
-- ===================================
CREATE TABLE IF NOT EXISTS user_kyc (
    id SERIAL PRIMARY KEY,
    user_id INTEGER UNIQUE NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    document_type VARCHAR(50),
    document_number VARCHAR(50),
    document_front_url VARCHAR(500),
    document_back_url VARCHAR(500),
    selfie_url VARCHAR(500),
    full_name VARCHAR(255),
    date_of_birth BIGINT,
    address VARCHAR(500),
    city VARCHAR(100),
    country VARCHAR(100),
    status VARCHAR(50) DEFAULT 'pending',
    rejection_reason TEXT,
    verified_at BIGINT,
    expires_at BIGINT,
    created_at BIGINT NOT NULL
);

CREATE INDEX idx_user_kyc_status ON user_kyc(status);

-- ===================================
-- TABLA: bank_accounts
-- ===================================
CREATE TABLE IF NOT EXISTS bank_accounts (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    account_type VARCHAR(50),
    bank_name VARCHAR(255),
    account_number VARCHAR(255),
    routing_number VARCHAR(50),
    account_holder_name VARCHAR(255),
    currency VARCHAR(3) DEFAULT 'PEN',
    is_default BOOLEAN DEFAULT false,
    is_active BOOLEAN DEFAULT true,
    verification_status VARCHAR(50) DEFAULT 'pending',
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE INDEX idx_bank_accounts_user_id ON bank_accounts(user_id);

-- ===================================
-- TABLA: ratings
-- ===================================
CREATE TABLE IF NOT EXISTS ratings (
    id SERIAL PRIMARY KEY,
    from_user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    to_user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reservation_id INTEGER REFERENCES reservations(id) ON DELETE SET NULL,
    rating INTEGER NOT NULL,
    comment TEXT,
    category VARCHAR(100),
    created_at BIGINT NOT NULL
);

CREATE INDEX idx_ratings_from_user ON ratings(from_user_id);
CREATE INDEX idx_ratings_to_user ON ratings(to_user_id);

-- ===================================
-- TABLA: reviews
-- ===================================
CREATE TABLE IF NOT EXISTS reviews (
    id SERIAL PRIMARY KEY,
    place_id INTEGER NOT NULL REFERENCES places(id) ON DELETE CASCADE,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    rating INTEGER NOT NULL,
    title VARCHAR(255),
    comment TEXT,
    photo_urls TEXT,
    helpful_count INTEGER DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE INDEX idx_reviews_place_id ON reviews(place_id);
CREATE INDEX idx_reviews_user_id ON reviews(user_id);

-- ===================================
-- TABLA: transactions
-- ===================================
CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) DEFAULT 'PEN',
    status VARCHAR(50) DEFAULT 'pending',
    description TEXT,
    reference_id INTEGER,
    reference_type VARCHAR(50),
    payment_method VARCHAR(50),
    transaction_hash VARCHAR(255) UNIQUE,
    created_at BIGINT NOT NULL,
    completed_at BIGINT
);

CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_type ON transactions(type);

-- ===================================
-- TABLA: disputes
-- ===================================
CREATE TABLE IF NOT EXISTS disputes (
    id SERIAL PRIMARY KEY,
    reservation_id INTEGER REFERENCES reservations(id) ON DELETE CASCADE,
    initiated_by_user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    reason VARCHAR(255),
    description TEXT,
    status VARCHAR(50) DEFAULT 'open',
    resolution TEXT,
    evidence_urls TEXT,
    assigned_to_admin_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    created_at BIGINT NOT NULL,
    resolved_at BIGINT
);

CREATE INDEX idx_disputes_status ON disputes(status);

-- ===================================
-- TABLA: complaints
-- ===================================
CREATE TABLE IF NOT EXISTS complaints (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    complaint_against_user_id INTEGER REFERENCES users(id) ON DELETE SET NULL,
    complaint_against_place_id INTEGER REFERENCES places(id) ON DELETE SET NULL,
    category VARCHAR(100) NOT NULL,
    title VARCHAR(255),
    description TEXT,
    severity VARCHAR(50) DEFAULT 'low',
    status VARCHAR(50) DEFAULT 'submitted',
    attachment_urls TEXT,
    resolution_notes TEXT,
    created_at BIGINT NOT NULL,
    resolved_at BIGINT
);

CREATE INDEX idx_complaints_status ON complaints(status);
CREATE INDEX idx_complaints_severity ON complaints(severity);

-- ===================================
-- TABLA: notifications
-- ===================================
CREATE TABLE IF NOT EXISTS notifications (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    type VARCHAR(100) NOT NULL,
    title VARCHAR(255),
    message TEXT,
    data TEXT,
    reference_type VARCHAR(50),
    reference_id INTEGER,
    is_read BOOLEAN DEFAULT false,
    read_at BIGINT,
    created_at BIGINT NOT NULL
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);

-- ===================================
-- DATOS DE EJEMPLO
-- ===================================

-- Insertar usuarios
INSERT INTO users (name, email, password_hash, phone, district, is_premium, kyc_status, created_at, updated_at)
VALUES 
    ('Juan García', 'juan@example.com', '$2a$10$example_hash_1', '987654321', 'Miraflores', true, 'approved', 1686700000000, 1686700000000),
    ('María López', 'maria@example.com', '$2a$10$example_hash_2', '987654322', 'San Isidro', false, 'pending', 1686710000000, 1686710000000),
    ('Carlos Ruiz', 'carlos@example.com', '$2a$10$example_hash_3', '987654323', 'Barranco', true, 'approved', 1686720000000, 1686720000000)
ON CONFLICT DO NOTHING;

-- Insertar lugares
INSERT INTO places (name, sport_type, category, lat, lng, address, description, services, owner_id, created_at, updated_at)
VALUES 
    ('Complejo Deportivo Miraflores', 'futbol', 'field', -12.1181, -77.0302, 'Av. Principal 123', 'Canchas de fútbol', 'seguridad,baños,iluminación', 1, 1686700000000, 1686700000000),
    ('Pista de Running Lima', 'running', 'trayecto', -12.0975, -77.0250, 'Parque Central', 'Ruta para correr 5km', 'seguridad,bebederos', 2, 1686710000000, 1686710000000),
    ('Piscina Olímpica', 'natacion', 'field', -12.0800, -77.0300, 'Av. Deportes 456', 'Piscina olímpica', 'seguridad,baños,vestuarios', 3, 1686720000000, 1686720000000)
ON CONFLICT DO NOTHING;

-- ===================================
-- Base de datos inicializada correctamente
-- ===================================
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO alex;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO alex;
