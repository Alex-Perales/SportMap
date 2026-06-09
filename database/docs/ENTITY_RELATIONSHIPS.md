# 📊 Diagrama de Relaciones - Base de Datos SportMap

## Diagrama General

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          CORE TABLES (NÚCLEO)                               │
└─────────────────────────────────────────────────────────────────────────────┘

            ┌──────────────────┐
            │      USERS       │
            ├──────────────────┤
            │ id (PK)          │
            │ name             │
            │ email (UNIQUE)   │
            │ password_hash    │
            │ district         │
            │ kyc_status       │
            │ rating           │
            │ is_premium       │
            │ created_at       │
            └──────────────────┘
                      │
        ┌─────────────┼─────────────┬─────────────┐
        │             │             │             │
        ▼             ▼             ▼             ▼
    ┌────────┐  ┌──────────────┐  ┌─────────┐  ┌────────┐
    │PLACES  │  │RESERVATIONS  │  │PRODUCTS │  │MEDALS  │
    ├────────┤  ├──────────────┤  ├─────────┤  ├────────┤
    │id (PK) │  │id (PK)       │  │id (PK)  │  │id (PK) │
    │name    │  │user_id (FK)  │  │name     │  │user_id │
    │sport   │  │place_id (FK) │  │price    │  │name    │
    │lat/lng │  │date          │  │stock    │  │earned  │
    │owner   │  │status        │  │vendor   │  │tier    │
    └────────┘  │price         │  └─────────┘  └────────┘
        │       └──────────────┘       │
        │             │                 ▼
        │             │          ┌──────────────┐
        │             │          │ CART_ITEMS   │
        │             │          ├──────────────┤
        │             │          │id            │
        │             │          │user_id (FK)  │
        │             │          │product_id    │
        │             │          │quantity      │
        │             │          └──────────────┘
        │             │
        ▼             ▼
    ┌────────┐  ┌──────────────┐
    │ACTVTIES│  │TRANSACTIONS  │
    └────────┘  └──────────────┘
        │
        │
        ▼
    ┌──────────┐
    │ REVIEWS  │
    └──────────┘

┌─────────────────────────────────────────────────────────────────────────────┐
│                    P2P & SOCIAL TABLES (INTERACCIÓN)                        │
└─────────────────────────────────────────────────────────────────────────────┘

    ┌──────────┐          ┌──────────┐          ┌────────────┐
    │ RATINGS  │          │ REVIEWS  │          │BANK_ACCTS  │
    ├──────────┤          ├──────────┤          ├────────────┤
    │from_id   │          │place_id  │          │user_id (FK)│
    │to_id (FK)│          │user_id   │          │account_num │
    │rating    │          │rating    │          │status      │
    │comment   │          │comment   │          │is_default  │
    └──────────┘          └──────────┘          └────────────┘

    ┌──────────┐          ┌──────────┐          ┌────────────┐
    │DISPUTES  │          │COMPLAINTS│          │USER_KYC    │
    ├──────────┤          ├──────────┤          ├────────────┤
    │resv_id   │          │user_id   │          │user_id(FK) │
    │init_user │          │against_id│          │doc_type    │
    │reason    │          │category  │          │doc_number  │
    │status    │          │severity  │          │status      │
    └──────────┘          └──────────┘          └────────────┘

    ┌─────────────────────────────────────────────────┐
    │         NOTIFICATIONS                          │
    ├─────────────────────────────────────────────────┤
    │user_id (FK)                                     │
    │type (reservation_confirmed, rating_received...)│
    │message                                          │
    │is_read                                          │
    │reference_id                                     │
    └─────────────────────────────────────────────────┘
```

---

## 🔗 Relaciones Detalladas

### **1. users → places** (1:N)
- Un usuario puede ser propietario de múltiples lugares
- Campo FK: `places.owner_id`

### **2. users → activities** (1:N)
- Un usuario registra múltiples actividades
- Campo FK: `activities.user_id`

### **3. users → reservations** (1:N)
- Un usuario hace múltiples reservas
- Campo FK: `reservations.user_id`

### **4. users → cart_items** (1:N)
- Un usuario tiene múltiples items en carrito
- Campo FK: `cart_items.user_id`

### **5. users → medals** (1:N)
- Un usuario obtiene múltiples medallas
- Campo FK: `medals.user_id`

### **6. users → ratings** (1:N - Doble relación)**
- Un usuario da calificaciones: `ratings.from_user_id`
- Un usuario recibe calificaciones: `ratings.to_user_id`

### **7. users → reviews** (1:N)
- Un usuario escribe múltiples reseñas
- Campo FK: `reviews.user_id`

### **8. users → transactions** (1:N)
- Un usuario realiza múltiples transacciones
- Campo FK: `transactions.user_id`

### **9. users → bank_accounts** (1:N)
- Un usuario vincula múltiples cuentas bancarias
- Campo FK: `bank_accounts.user_id`

### **10. users → user_kyc** (1:1)
- Un usuario tiene un único registro KYC
- Campo FK: `user_kyc.user_id` (UNIQUE)

### **11. users → disputes** (1:N)
- Un usuario inicia múltiples disputas
- Campo FK: `disputes.initiated_by_user_id`

### **12. users → complaints** (1:N)**
- Un usuario reporta múltiples quejas: `complaints.user_id`
- Un usuario puede ser reportado: `complaints.complaint_against_user_id`

### **13. users → notifications** (1:N)
- Un usuario recibe múltiples notificaciones
- Campo FK: `notifications.user_id`

### **14. places → reservations** (1:N)
- Un lugar tiene múltiples reservas
- Campo FK: `reservations.place_id`

### **15. places → activities** (1:N)
- Una actividad ocurre en un lugar (opcional)
- Campo FK: `activities.place_id`

### **16. places → reviews** (1:N)
- Un lugar recibe múltiples reseñas
- Campo FK: `reviews.place_id`

### **17. products → cart_items** (1:N)
- Un producto puede estar en múltiples carritos
- Campo FK: `cart_items.product_id`

### **18. products → transactions** (1:N)
- Un producto se vende múltiples veces
- Campo FK: `transactions.reference_id` (cuando type='purchase')

### **19. reservations → ratings** (1:N)
- Una reserva genera múltiples calificaciones (opcional)
- Campo FK: `ratings.reservation_id`

### **20. reservations → disputes** (1:1)
- Una reserva puede tener una disputa
- Campo FK: `disputes.reservation_id`

### **21. reservations → transactions** (1:N)
- Una reserva genera una o múltiples transacciones
- Campo FK: `transactions.reference_id` (cuando type='reservation')

---

## 📋 Tabla de Cascadas (Foreign Keys)

| Tabla | FK → Tabla | Comportamiento ON DELETE |
|-------|-----------|-------------------------|
| places | users | SET NULL |
| activities | users | CASCADE |
| activities | places | SET NULL |
| reservations | users | CASCADE |
| reservations | places | CASCADE |
| cart_items | users | CASCADE |
| cart_items | products | CASCADE |
| medals | users | CASCADE |
| bank_accounts | users | CASCADE |
| ratings | users (both) | CASCADE |
| ratings | reservations | SET NULL |
| reviews | places | CASCADE |
| reviews | users | CASCADE |
| transactions | users | CASCADE |
| disputes | reservations | CASCADE |
| disputes | users | CASCADE |
| complaints | users (both) | CASCADE |
| complaints | places | SET NULL |
| notifications | users | CASCADE |
| user_kyc | users | CASCADE |

---

## 🔍 Flujos de Datos Principales

### **Flujo 1: Reserva de Lugar**
```
User → Creates → Reservation
                      ↓
                   Transaction (payment)
                      ↓
                  Notification (confirmation)
                      ↓
                   Rating/Review (después completar)
```

### **Flujo 2: Compra de Producto**
```
User → Adds to → CartItem
                      ↓
                 Checkout → Transaction
                      ↓
                  Notification (order confirmed)
                      ↓
                   Review (después recibir)
```

### **Flujo 3: Disputa/Queja**
```
Reservation → Issue → Dispute → Admin Review → Resolution
                ↓
           Complaint → Severity Check → Investigation → Close
```

### **Flujo 4: KYC & Verificación**
```
User → Submit → UserKyc (pending)
                      ↓
                   Admin Review
                      ↓
                   Approved/Rejected
                      ↓
                 User Update (is_premium eligible)
```

### **Flujo 5: Notificaciones**
```
Reservation/Transaction/Rating/Medal → Notification → User Inbox
                                              ↓
                                         Mark as Read
                                              ↓
                                           Archive/Delete
```

---

## 📊 Cardinalidad de Relaciones

```
users (1) ──────────────────────→ (N) activities
users (1) ──────────────────────→ (N) reservations
users (1) ──────────────────────→ (N) cart_items
users (1) ──────────────────────→ (N) medals
users (1) ──────────────────────→ (N) ratings (from/to)
users (1) ──────────────────────→ (N) reviews
users (1) ──────────────────────→ (N) transactions
users (1) ──────────────────────→ (N) bank_accounts
users (1) ──────────────────────→ (1) user_kyc
users (1) ──────────────────────→ (N) disputes
users (1) ──────────────────────→ (N) complaints
users (1) ──────────────────────→ (N) notifications

places (1) ────────────────────→ (N) reservations
places (1) ────────────────────→ (N) reviews
places (1) ────────────────────→ (N) activities

products (1) ──────────────────→ (N) cart_items

reservations (1) ──────────────→ (N) ratings
reservations (1) ──────────────→ (1) disputes
reservations (1) ──────────────→ (N) transactions
```

---

## 🎯 Optimizaciones

### **Índices Críticos**
- ✅ `users.email` — Búsqueda de usuario
- ✅ `places.lat, places.lng` — Queries geoespaciales
- ✅ `activities.user_id, activities.date` — Historial
- ✅ `reservations.user_id, reservations.status` — Reservas activas
- ✅ `transactions.user_id, transactions.created_at` — Historial financiero
- ✅ `notifications.user_id, notifications.is_read` — Notificaciones sin leer

### **Desnormalización Intencional**
Se almacenan algunos datos duplicados para mejor rendimiento:
- `reservations.place_name` (desnormalizado de `places`)
- `cart_items.product_name` (desnormalizado de `products`)
- `users.rating` (calculado de `ratings`)

---

## 🔐 Consideraciones de Seguridad

- ✅ Passwords hasheados en `users.password_hash`
- ✅ Números de cuenta encriptados en `bank_accounts.account_number`
- ✅ Documentos de identidad hasheados/encriptados en `user_kyc`
- ✅ Timestamps en todas las transacciones para auditoría
- ✅ Soft deletes posibles con estatus "cancelled", "archived"

