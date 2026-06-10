# 📚 Índice de Documentación - SportMap Database v2.0

Bienvenido a la documentación completa de la base de datos SportMap. Usa esta guía para navegar rápidamente a lo que necesitas.

---

## 🚀 **EMPEZAR AQUÍ**

### Para usuarios nuevos
👉 **[QUICK_START.md](QUICK_START.md)** — Guía de 5 minutos para empezar  
→ Compilar proyecto, primeros DAOs, casos de uso comunes

### Para entender la estructura
👉 **[COMPLETION_SUMMARY.md](COMPLETION_SUMMARY.md)** — Resumen de qué se creó  
→ 15 tablas, 15 entidades, 15 DAOs, estadísticas completas

---

## 📖 **DOCUMENTACIÓN TÉCNICA**

### Estructura Completa de la BD
📄 **[docs/DATABASE_STRUCTURE.md](docs/DATABASE_STRUCTURE.md)**
- ✅ Especificación de cada tabla
- ✅ Campos, tipos, restricciones
- ✅ Índices y optimizaciones
- ✅ Ejemplos de datos
- **Leer si:** Necesitas detalles técnicos de una tabla específica

### Relaciones entre Entidades
📄 **[docs/ENTITY_RELATIONSHIPS.md](docs/ENTITY_RELATIONSHIPS.md)**
- ✅ Diagramas ASCII
- ✅ Relaciones 1:N y 1:1
- ✅ Cascadas (ON DELETE)
- ✅ Cardinalidad
- **Leer si:** Quieres entender cómo se conectan las tablas

### Implementación de Repositorios
📄 **[docs/REPOSITORY_IMPLEMENTATION.md](docs/REPOSITORY_IMPLEMENTATION.md)**
- ✅ Ejemplos de 6 repositorios
- ✅ Patrones y mejores prácticas
- ✅ Integración con ViewModels
- ✅ Testing unitario
- **Leer si:** Quieres implementar la capa de repositorios

---

## 🗂️ **SCRIPTS SQL**

Ubicación: `database/sql/`

| Script | Tabla | Propósito |
|--------|-------|----------|
| `001_users.sql` | users | Usuarios del sistema |
| `002_places.sql` | places | Lugares deportivos |
| `003_activities.sql` | activities | Actividades registradas |
| `004_reservations.sql` | reservations | Reservas de lugares |
| `005_products.sql` | products | Productos tienda |
| `006_cart_items.sql` | cart_items | Carrito compras |
| `007_medals.sql` | medals | Gamificación |
| `008_user_kyc.sql` | user_kyc | Verificación identidad |
| `009_bank_accounts.sql` | bank_accounts | Cuentas bancarias |
| `010_ratings.sql` | ratings | Calificaciones P2P |
| `011_reviews.sql` | reviews | Reseñas lugares |
| `012_transactions.sql` | transactions | Transacciones |
| `013_disputes.sql` | disputes | Disputas |
| `014_complaints.sql` | complaints | Quejas |
| `015_notifications.sql` | notifications | Notificaciones |
| `master.sql` | todos | Ejecuta todos en orden |

**Uso:** `sqlite3 sportmap.db < master.sql`

---

## 💾 **CÓDIGO FUENTE**

### Entidades Room
📂 `app/src/main/java/com/tunalex/sportmap/data/local/entity/`

```
Originales (7):
✅ UserEntity.kt
✅ PlaceEntity.kt
✅ ActivityEntity.kt
✅ ReservationEntity.kt
✅ ProductEntity.kt
✅ CartItemEntity.kt
✅ MedalEntity.kt

Nuevas (8):
✅ UserKycEntity.kt
✅ BankAccountEntity.kt
✅ RatingEntity.kt
✅ ReviewEntity.kt
✅ TransactionEntity.kt
✅ DisputeEntity.kt
✅ ComplaintEntity.kt
✅ NotificationEntity.kt
```

### Data Access Objects (DAOs)
📂 `app/src/main/java/com/tunalex/sportmap/data/local/dao/`

```
Originales (7):
✅ UserDao.kt
✅ PlaceDao.kt
✅ ActivityDao.kt
✅ ReservationDao.kt
✅ ProductDao.kt
✅ CartDao.kt
✅ MedalDao.kt

Nuevos (8):
✅ UserKycDao.kt
✅ BankAccountDao.kt
✅ RatingDao.kt
✅ ReviewDao.kt
✅ TransactionDao.kt
✅ DisputeDao.kt
✅ ComplaintDao.kt
✅ NotificationDao.kt
```

### Base de Datos Principal
📄 `app/src/main/java/com/tunalex/sportmap/data/local/SportMapDatabase.kt`
- Anotación @Database con 15 entidades
- Version 2.0
- 15 métodos de acceso a DAOs

---

## 🎯 **CASOS DE USO POR FEATURE**

### 1. Autenticación y Perfil
```
→ users
  → user_kyc (verificación)
  → bank_accounts (pagos)
```
📖 Ver: [QUICK_START.md - Verificación KYC](QUICK_START.md#2-vincular-cuenta-bancaria)

### 2. Reservas y Transacciones
```
→ reservations
  → transactions (pago)
  → notifications (confirmación)
  → ratings (después)
  → disputes (si hay problema)
```
📖 Ver: [REPOSITORY_IMPLEMENTATION.md - ReservationRepository](docs/REPOSITORY_IMPLEMENTATION.md#2-reservationrepository)

### 3. Marketplace/Tienda
```
→ products
  → cart_items
  → transactions
  → reviews (por producto)
```
📖 Ver: [QUICK_START.md - Transacciones](QUICK_START.md#paso-4-registrar-una-transacción)

### 4. Social/Gamificación
```
→ ratings (usuario a usuario)
  → reviews (usuario a lugar)
  → medals (logros)
  → complaints (reportes)
```
📖 Ver: [QUICK_START.md - Calificaciones](QUICK_START.md#paso-5-calificar-a-un-usuario)

### 5. Notificaciones
```
→ notifications (cualquier evento)
  → read/unread tracking
  → referencias a otros objetos
```
📖 Ver: [REPOSITORY_IMPLEMENTATION.md - NotificationRepository](docs/REPOSITORY_IMPLEMENTATION.md#5-notificationrepository)

### 6. Soporte/Admin
```
→ disputes (conflictos en reservas)
  → complaints (reportes formales)
  → admin resolution workflow
```
📖 Ver: [QUICK_START.md - Disputas y Quejas](QUICK_START.md#4-reportar-disputa)

---

## 🔍 **BUSCAR POR TABLA**

### users
- Documentación: [DATABASE_STRUCTURE.md#users](docs/DATABASE_STRUCTURE.md#1-users--gestión-de-usuarios)
- Entity: [UserEntity.kt](../app/src/main/java/com/tunalex/sportmap/data/local/entity/UserEntity.kt)
- DAO: [UserDao.kt](../app/src/main/java/com/tunalex/sportmap/data/local/dao/UserDao.kt)

### notifications
- Documentación: [DATABASE_STRUCTURE.md#notifications](docs/DATABASE_STRUCTURE.md#15-notifications--notificaciones)
- Entity: [NotificationEntity.kt](../app/src/main/java/com/tunalex/sportmap/data/local/entity/NotificationEntity.kt)
- DAO: [NotificationDao.kt](../app/src/main/java/com/tunalex/sportmap/data/local/dao/NotificationDao.kt)
- Ejemplo: [QUICK_START.md#paso-2](QUICK_START.md#paso-2-usar-un-dao-existente)

### transactions
- Documentación: [DATABASE_STRUCTURE.md#transactions](docs/DATABASE_STRUCTURE.md#12-transactions--transacciones)
- Entity: [TransactionEntity.kt](../app/src/main/java/com/tunalex/sportmap/data/local/entity/TransactionEntity.kt)
- DAO: [TransactionDao.kt](../app/src/main/java/com/tunalex/sportmap/data/local/dao/TransactionDao.kt)
- Repository: [REPOSITORY_IMPLEMENTATION.md#3-transactionrepository](docs/REPOSITORY_IMPLEMENTATION.md#3-transactionrepository)

*Para otras tablas, reemplaza el nombre en las URLs anteriores.*

---

## 📊 **DIAGRAMAS Y VISUALIZACIÓN**

Todos los diagramas están en **[ENTITY_RELATIONSHIPS.md](docs/ENTITY_RELATIONSHIPS.md)**

```
┌──────────────────────────────────────┐
│          CORE TABLES                 │
├──────────────────────────────────────┤
│ users ←→ places, reservations,       │
│         activities, products...       │
├──────────────────────────────────────┤
│    P2P & SOCIAL TABLES               │
├──────────────────────────────────────┤
│ ratings, reviews, complaints,        │
│ disputes, notifications              │
└──────────────────────────────────────┘
```

---

## 🚀 **PRÓXIMOS PASOS RECOMENDADOS**

### 1️⃣ Setup Inicial (10 min)
- [ ] Lee [QUICK_START.md](QUICK_START.md)
- [ ] Compila el proyecto
- [ ] Verifica que no hay errores

### 2️⃣ Entender la Estructura (30 min)
- [ ] Lee [COMPLETION_SUMMARY.md](COMPLETION_SUMMARY.md)
- [ ] Revisa [docs/ENTITY_RELATIONSHIPS.md](docs/ENTITY_RELATIONSHIPS.md)
- [ ] Abre 2-3 archivos de Entity en el IDE

### 3️⃣ Implementar Features (1-2 horas)
- [ ] Lee casos de uso relevantes en [QUICK_START.md](QUICK_START.md)
- [ ] Implementa repositorios (ver [docs/REPOSITORY_IMPLEMENTATION.md](docs/REPOSITORY_IMPLEMENTATION.md))
- [ ] Crea ViewModels
- [ ] Construye UI

### 4️⃣ Testing (30 min)
- [ ] Escribe tests unitarios para DAOs
- [ ] Prueba repositorios
- [ ] Valida flujos completos

---

## 💡 **TIPS ÚTILES**

### Buscar una Entity
```bash
# En terminal, desde raíz
find . -name "*Entity.kt" | grep data/local/entity
```

### Ver todas las queries de un DAO
```bash
# Abrir DAO en el IDE
# Ctrl+Click en cualquier método para ver implementación
```

### Entender una relación
```
1. Encuentra las dos tablas
2. Abre ENTITY_RELATIONSHIPS.md
3. Busca el diagrama correspondiente
4. Lee la cardinalidad (1:N, 1:1)
5. Verifica las cascadas (ON DELETE)
```

---

## 🆘 **AYUDA RÁPIDA**

**¿Cómo insertar un registro?**
→ [QUICK_START.md#paso-3](QUICK_START.md#paso-3-crear-una-notificación)

**¿Cómo leer datos?**
→ [QUICK_START.md#paso-2](QUICK_START.md#paso-2-usar-un-dao-existente)

**¿Cómo crear un Repositorio?**
→ [docs/REPOSITORY_IMPLEMENTATION.md](docs/REPOSITORY_IMPLEMENTATION.md)

**¿Cómo implementar transacciones?**
→ [docs/REPOSITORY_IMPLEMENTATION.md#2-reservationrepository](docs/REPOSITORY_IMPLEMENTATION.md#2-reservationrepository)

**¿Cuáles son las FK?**
→ [docs/ENTITY_RELATIONSHIPS.md#-relaciones-principales](docs/ENTITY_RELATIONSHIPS.md#-relaciones-principales)

**¿Qué tablas son nuevas?**
→ [COMPLETION_SUMMARY.md#3-entidades-kotlin-8-nuevas-entidades](COMPLETION_SUMMARY.md#3-entidades-kotlin-8-nuevas-entidades)

---

## 📞 **CONTACTO Y SOPORTE**

Si tienes dudas:

1. Busca en esta documentación
2. Revisa los comentarios en los archivos `.kt`
3. Consulta los ejemplos en [QUICK_START.md](QUICK_START.md)
4. Lee la documentación técnica completa en [docs/DATABASE_STRUCTURE.md](docs/DATABASE_STRUCTURE.md)

---

## 📦 **SUMMARY**

| Aspecto | Cantidad |
|--------|----------|
| Tablas | 15 |
| Entidades Kotlin | 15 |
| DAOs | 15 |
| Scripts SQL | 16 |
| Documentos | 7 |
| Líneas de código | ~3,500+ |
| Relaciones (FK) | 21+ |
| Índices | 25+ |

---

**¡Bienvenido a SportMap Database v2.0! 🚀**

Última actualización: Junio 2026  
Versión: 2.0  
Estado: Listo para Producción

Empieza con [QUICK_START.md](QUICK_START.md) →
