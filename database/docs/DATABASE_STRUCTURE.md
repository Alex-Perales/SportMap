# Estructura de Base de Datos - SportMap

## 📋 Visión General

SportMap es una plataforma móvil para gestionar actividades deportivas, reservar espacios deportivos, comercializar productos y gamificar la experiencia del usuario. La estructura de BD está diseñada siguiendo el patrón del proyecto P2P Fintech, adaptado al contexto de un marketplace deportivo.

---

## 🏗️ Estructura General

```
database/
├── sql/
│   ├── 001_users.sql              → Tabla de usuarios
│   ├── 002_places.sql             → Lugares deportivos
│   ├── 003_activities.sql         → Actividades registradas
│   ├── 004_reservations.sql       → Reservaciones de lugares
│   ├── 005_products.sql           → Productos de la tienda
│   ├── 006_cart_items.sql         → Carrito de compras
│   ├── 007_medals.sql             → Medallas/logros
│   ├── 008_user_kyc.sql           → Datos de verificación de identidad
│   ├── 009_bank_accounts.sql      → Cuentas bancarias
│   ├── 010_ratings.sql            → Calificaciones entre usuarios
│   ├── 011_reviews.sql            → Reseñas de lugares
│   ├── 012_transactions.sql       → Transacciones (pagos, compras)
│   ├── 013_disputes.sql           → Disputas sobre reservas
│   ├── 014_complaints.sql         → Quejas formales
│   └── 015_notifications.sql      → Notificaciones del sistema
├── docs/
│   └── DATABASE_STRUCTURE.md      → Este archivo
└── README.md                       → Instrucciones de uso
```

---

## 📊 Tablas de la Base de Datos

### 1. **users** — Gestión de Usuarios
Información básica y datos de cuenta de los usuarios.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único del usuario |
| name | TEXT NOT NULL | Nombre completo |
| email | TEXT UNIQUE NOT NULL | Email único |
| password_hash | TEXT NOT NULL | Hash seguro de la contraseña |
| phone | TEXT | Teléfono de contacto |
| district | TEXT | Distrito/zona (ej. Miraflores) |
| bio | TEXT | Biografía del usuario |
| profile_image_url | TEXT | URL de foto de perfil |
| is_premium | BOOLEAN | ¿Usuario premium? |
| kyc_status | TEXT | Estado KYC: pending, approved, rejected |
| rating | REAL | Calificación promedio (1-5) |
| total_activities | INTEGER | Total de actividades realizadas |
| created_at | INTEGER | Timestamp de creación |
| updated_at | INTEGER | Timestamp de última actualización |

---

### 2. **places** — Lugares Deportivos
Espacios deportivos disponibles para usar y reservar.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único del lugar |
| name | TEXT NOT NULL | Nombre del lugar |
| sport_type | TEXT | Tipo de deporte (futbol, voley, running, natacion, ciclismo, bienestar) |
| category | TEXT | Categoría (field, trayecto, wellness) |
| lat | REAL NOT NULL | Latitud |
| lng | REAL NOT NULL | Longitud |
| address | TEXT | Dirección completa |
| is_private | BOOLEAN | ¿Lugar privado? |
| description | TEXT | Descripción detallada |
| services | TEXT | Servicios CSV (seguridad,baños,iluminación) |
| photo_urls | TEXT | URLs de fotos (CSV) |
| rating | REAL | Calificación promedio |
| price_per_hour | REAL | Precio por hora de reserva |
| air_quality_index | INTEGER | Índice de calidad del aire |
| owner_id | INTEGER | ID del propietario (FK users) |
| total_reviews | INTEGER | Total de reseñas |
| created_at | INTEGER | Timestamp de creación |
| updated_at | INTEGER | Timestamp de última actualización |

---

### 3. **activities** — Actividades Registradas
Registro de actividades realizadas por usuarios.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único de la actividad |
| user_id | INTEGER NOT NULL | FK a users |
| type | TEXT NOT NULL | Tipo (running, cycling, futbol, etc.) |
| distance_km | REAL | Distancia en km |
| duration_minutes | INTEGER | Duración en minutos |
| calories_burned | INTEGER | Calorías quemadas |
| place_id | INTEGER | FK a places (opcional) |
| route_coordinates | TEXT | Coordenadas JSON de la ruta |
| date | INTEGER NOT NULL | Timestamp de la actividad |
| created_at | INTEGER | Timestamp de registro |

---

### 4. **reservations** — Reservaciones de Lugares
Sistema de reserva de espacios deportivos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único de la reserva |
| user_id | INTEGER NOT NULL | FK a users |
| place_id | INTEGER NOT NULL | FK a places |
| place_name | TEXT | Nombre del lugar (desnormalizado) |
| reservation_date | INTEGER | Fecha de la reserva (timestamp) |
| start_time | TEXT | Hora inicio (HH:MM) |
| end_time | TEXT | Hora fin (HH:MM) |
| people_count | INTEGER | Cantidad de personas |
| total_price | REAL | Precio total |
| status | TEXT | Estado (confirmed, pending, cancelled, completed) |
| cancellation_reason | TEXT | Razón de cancelación |
| qr_code | TEXT | Código QR de verificación |
| created_at | INTEGER | Timestamp de creación |
| updated_at | INTEGER | Timestamp de última actualización |

---

### 5. **products** — Productos de la Tienda
Catálogo de productos deportivos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único del producto |
| name | TEXT NOT NULL | Nombre del producto |
| description | TEXT | Descripción detallada |
| price | REAL NOT NULL | Precio base |
| image_url | TEXT | URL de imagen principal |
| images_urls | TEXT | URLs adicionales (CSV) |
| category | TEXT | Categoría (calzado, balones, hidratación, accesorios) |
| sizes | TEXT | Tallas disponibles (CSV) |
| colors | TEXT | Colores disponibles (CSV) |
| stock | INTEGER | Cantidad en stock |
| is_on_sale | BOOLEAN | ¿Está en oferta? |
| discount_percent | INTEGER | Porcentaje de descuento |
| rating | REAL | Calificación promedio |
| total_reviews | INTEGER | Total de reseñas |
| vendor_id | INTEGER | FK a users (vendedor) |
| created_at | INTEGER | Timestamp de creación |
| updated_at | INTEGER | Timestamp de última actualización |

---

### 6. **cart_items** — Carrito de Compras
Items en el carrito de cada usuario.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único del item |
| user_id | INTEGER NOT NULL | FK a users |
| product_id | INTEGER NOT NULL | FK a products |
| product_name | TEXT | Nombre del producto (desnormalizado) |
| product_image_url | TEXT | Imagen del producto |
| unit_price | REAL | Precio unitario |
| quantity | INTEGER | Cantidad |
| selected_size | TEXT | Talla seleccionada |
| selected_color | TEXT | Color seleccionado |
| added_at | INTEGER | Timestamp de adición al carrito |

---

### 7. **medals** — Medallas/Logros
Sistema de gamificación con medallas y logros.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único |
| user_id | INTEGER NOT NULL | FK a users |
| name | TEXT NOT NULL | Nombre del logro |
| description | TEXT | Descripción del logro |
| icon_key | TEXT | Identificador del icono |
| condition | TEXT | Condición para conseguir (JSON) |
| earned | BOOLEAN | ¿Conseguida? |
| earned_date | INTEGER | Fecha de obtención |
| tier | TEXT | Nivel (bronze, silver, gold) |
| points | INTEGER | Puntos asociados |
| created_at | INTEGER | Timestamp de creación |

---

### 8. **user_kyc** — Verificación de Identidad (KYC)
Datos de Know Your Customer para verificación.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único |
| user_id | INTEGER UNIQUE NOT NULL | FK a users |
| document_type | TEXT | Tipo (DNI, pasaporte, etc.) |
| document_number | TEXT | Número del documento |
| document_front_url | TEXT | Foto del anverso |
| document_back_url | TEXT | Foto del reverso |
| selfie_url | TEXT | Foto de selfie con documento |
| full_name | TEXT | Nombre completo verificado |
| date_of_birth | INTEGER | Fecha de nacimiento |
| address | TEXT | Dirección verificada |
| city | TEXT | Ciudad |
| country | TEXT | País |
| status | TEXT | Estado (pending, approved, rejected) |
| rejection_reason | TEXT | Razón de rechazo |
| verified_at | INTEGER | Timestamp de verificación |
| expires_at | INTEGER | Fecha de expiración |
| created_at | INTEGER | Timestamp de creación |

---

### 9. **bank_accounts** — Cuentas Bancarias
Cuentas vinculadas para pagos y retiros.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único |
| user_id | INTEGER NOT NULL | FK a users |
| account_type | TEXT | Tipo (checking, savings, wallet) |
| bank_name | TEXT | Nombre del banco |
| account_number | TEXT | Número de cuenta (encriptado) |
| routing_number | TEXT | Código de enrutamiento |
| account_holder_name | TEXT | Titular de la cuenta |
| currency | TEXT | Moneda (PEN, USD, etc.) |
| is_default | BOOLEAN | ¿Cuenta por defecto? |
| is_active | BOOLEAN | ¿Está activa? |
| verification_status | TEXT | Estado (pending, verified) |
| created_at | INTEGER | Timestamp de creación |
| updated_at | INTEGER | Timestamp de última actualización |

---

### 10. **ratings** — Calificaciones entre Usuarios
Sistema de calificaciones P2P.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único |
| from_user_id | INTEGER NOT NULL | FK a users (quien califica) |
| to_user_id | INTEGER NOT NULL | FK a users (quien es calificado) |
| reservation_id | INTEGER | FK a reservations (contexto) |
| rating | INTEGER | Calificación (1-5 estrellas) |
| comment | TEXT | Comentario |
| category | TEXT | Categoría (cleanliness, communication, punctuality) |
| created_at | INTEGER | Timestamp |

---

### 11. **reviews** — Reseñas de Lugares
Reseñas detalladas de lugares deportivos.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único |
| place_id | INTEGER NOT NULL | FK a places |
| user_id | INTEGER NOT NULL | FK a users |
| rating | INTEGER | Calificación (1-5) |
| title | TEXT | Título de la reseña |
| comment | TEXT | Contenido de la reseña |
| photo_urls | TEXT | URLs de fotos (CSV) |
| helpful_count | INTEGER | Votos útiles |
| created_at | INTEGER | Timestamp de creación |
| updated_at | INTEGER | Timestamp de última actualización |

---

### 12. **transactions** — Transacciones
Registro de pagos y compras en la plataforma.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único |
| user_id | INTEGER NOT NULL | FK a users |
| type | TEXT | Tipo (purchase, reservation, withdrawal) |
| amount | REAL | Monto |
| currency | TEXT | Moneda |
| status | TEXT | Estado (pending, completed, failed, refunded) |
| description | TEXT | Descripción |
| reference_id | INTEGER | ID de referencia (FK order, reservation, etc.) |
| reference_type | TEXT | Tipo de referencia (product, reservation) |
| payment_method | TEXT | Método (card, bank_transfer, wallet) |
| transaction_hash | TEXT | Hash único de transacción |
| created_at | INTEGER | Timestamp de creación |
| completed_at | INTEGER | Timestamp de completación |

---

### 13. **disputes** — Disputas
Gestión de conflictos sobre reservas o transacciones.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único |
| reservation_id | INTEGER | FK a reservations |
| initiated_by_user_id | INTEGER NOT NULL | FK a users |
| reason | TEXT | Razón de la disputa |
| description | TEXT | Descripción detallada |
| status | TEXT | Estado (open, in_review, resolved, closed) |
| resolution | TEXT | Resolución aplicada |
| evidence_urls | TEXT | URLs de evidencia (CSV) |
| assigned_to_admin_id | INTEGER | FK a users (admin) |
| created_at | INTEGER | Timestamp de creación |
| resolved_at | INTEGER | Timestamp de resolución |

---

### 14. **complaints** — Quejas Formales
Quejas de usuarios sobre servicios o usuarios.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único |
| user_id | INTEGER NOT NULL | FK a users (quejoso) |
| complaint_against_user_id | INTEGER | FK a users (sujeto de queja) |
| complaint_against_place_id | INTEGER | FK a places (lugar en cuestión) |
| category | TEXT | Categoría (harassment, fraud, bad_service, etc.) |
| title | TEXT | Título de la queja |
| description | TEXT | Descripción detallada |
| severity | TEXT | Gravedad (low, medium, high) |
| status | TEXT | Estado (submitted, reviewing, resolved, closed) |
| attachment_urls | TEXT | URLs de adjuntos (CSV) |
| resolution_notes | TEXT | Notas de resolución |
| created_at | INTEGER | Timestamp de creación |
| resolved_at | INTEGER | Timestamp de resolución |

---

### 15. **notifications** — Notificaciones
Sistema de notificaciones push y en-app.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| id | INTEGER PRIMARY KEY | ID único |
| user_id | INTEGER NOT NULL | FK a users |
| type | TEXT | Tipo (reservation_confirmed, rating_received, medal_earned, etc.) |
| title | TEXT | Título de la notificación |
| message | TEXT | Contenido del mensaje |
| data | TEXT | Datos JSON asociados |
| reference_type | TEXT | Tipo de referencia |
| reference_id | INTEGER | ID de referencia |
| is_read | BOOLEAN | ¿Leída? |
| read_at | INTEGER | Timestamp de lectura |
| created_at | INTEGER | Timestamp de creación |

---

## 🔑 Relaciones Principales

```
users
  ├── 1 → N activities
  ├── 1 → N reservations
  ├── 1 → N cart_items
  ├── 1 → N medals
  ├── 1 → 1 user_kyc
  ├── 1 → N bank_accounts
  ├── 1 → N ratings (from_user_id)
  ├── 1 → N ratings (to_user_id)
  ├── 1 → N reviews
  ├── 1 → N transactions
  ├── 1 → N disputes
  ├── 1 → N complaints
  └── 1 → N notifications

places
  ├── 1 → N reservations
  ├── 1 → N activities
  ├── 1 → N reviews
  └── 1 → N complaints

products
  ├── 1 → N cart_items
  └── 1 → N transactions

reservations
  ├── 1 → N ratings
  ├── 1 → N disputes
  └── 1 → N transactions
```

---

## 📋 Índices Recomendados

Para optimizar queries frecuentes, se crean índices en:
- `users.email` — búsqueda rápida de usuarios
- `places.lat, places.lng` — queries geoespaciales
- `activities.user_id, activities.date` — historial de usuario
- `reservations.user_id, reservations.status` — reservas activas
- `transactions.user_id, transactions.created_at` — historial de transacciones
- `notifications.user_id, notifications.is_read` — notificaciones no leídas

---

## 🛡️ Seguridad y Datos Sensibles

- ❌ **Nunca almacenar** números de tarjeta completos (usar tokens de pago)
- ❌ **Nunca almacenar** contraseñas en texto plano
- ✅ **Encriptar** números de cuenta bancaria
- ✅ **Hashear** documentos de identidad
- ✅ **Usar timestamps** en lugar de fechas legibles para queries
- ✅ **Implementar soft deletes** para datos críticos

---

## 📈 Escalabilidad Futura

Para futuras versiones:
- **Particionamiento** de tablas por date (transactions, activities)
- **Replicación** de BD en múltiples regiones
- **Caché** (Redis) para queries frecuentes
- **Data warehouse** para analytics

