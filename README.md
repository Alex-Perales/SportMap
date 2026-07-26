# SportMap

Aplicación Android para encontrar canchas, rutas de entrenamiento y espacios de bienestar cerca de ti. Reserva, entrena, compra en la tienda y mejora día a día — con un backend propio en FastAPI y un panel de administración web.

## Estructura del repositorio

```
SportMap/
├── sport_android/     # App Android (Kotlin + Jetpack Compose)
└── sport_backend/      # Backend (FastAPI + Postgres) y panel de administración
```

> El QR de Yape/Plin que se muestra en la app vive en
> `sport_android/src/main/res/drawable/qr_yape.jpg` — es el único que
> importa, ya empaquetado como recurso de la app.

## Stack tecnológico

**App Android**
- Kotlin 2.0 + Jetpack Compose + Material 3
- Room (base de datos local, offline-first)
- Navigation Compose
- Google Maps Compose + Directions API (rutas)
- Retrofit + OkHttp (consumo del backend)
- Firebase Cloud Messaging (notificaciones push)
- Coil (imágenes), DataStore (preferencias)
- minSdk 24 · targetSdk / compileSdk 35

**Backend**
- FastAPI + asyncpg (sin ORM) + PostgreSQL
- Panel de administración server-side (Jinja2 + sesiones)
- Supabase Storage para imágenes (con respaldo a disco local si no está configurado)
- Firebase Admin SDK para push
- Docker / Docker Compose para desarrollo local

## Cómo levantar el proyecto

### 1. Backend (Docker)

```bash
docker compose -f sport_backend/docker/docker-compose.yml up -d --build
```

Levanta 4 contenedores: la API (`:8000`), Postgres (`:5432`), pgAdmin (`:5050`) y Redis (`:6379`). Al arrancar, corre las migraciones y siembra datos de ejemplo (lugares, productos, anuncios y un usuario administrador).

Salud del servicio: `http://localhost:8000/health`

### 2. App Android

1. Abre `sport_android/` en Android Studio.
2. Espera el Gradle Sync.
3. En **debug**, la app apunta automáticamente al backend local (`10.0.2.2:8000` desde el emulador) — no hace falta configurar nada. En **release** apunta a producción (Railway).
4. Ejecuta ▶ en un emulador o dispositivo.

### 3. Panel de administración

`http://localhost:8000/admin/login`

Usuario sembrado por defecto (solo para desarrollo local — cámbialo antes de exponer esto en producción):
- **Correo:** `admin@gmail.com`
- **Contraseña:** `12345`

Desde ahí se administran:
| Sección | Qué permite |
|---|---|
| **Productos** | Crear/editar/borrar productos de la tienda, precio, foto, categoría, oferta/descuento |
| **Lugares** | Crear/editar/borrar canchas y espacios (nombre, deporte, coordenadas, foto, precio por hora) |
| **Pedidos** | Revisar comprobantes de pago, aprobar, rechazar (con motivo) o marcar como reembolsado |
| **Anuncios** | Editar las tarjetas de "Recomendados para ti" del Dashboard |

## Variables de entorno del backend

| Variable | Para qué | Si falta |
|---|---|---|
| `DATABASE_URL` | Conexión a Postgres | Requerida |
| `SUPABASE_URL` / `SUPABASE_SERVICE_KEY` | Subir fotos (comprobantes, perfiles, productos, lugares) a Supabase Storage | Las fotos se guardan en disco local del servidor en su lugar |
| `GMAIL_ADDRESS` / `GMAIL_APP_PASSWORD` | Enviar correos de confirmación/rechazo de pago | El envío se omite (se loguea, no rompe el flujo) |
| `FIREBASE_SERVICE_ACCOUNT_JSON` (o `FIREBASE_SERVICE_ACCOUNT_PATH`) | Notificaciones push al aprobar/rechazar un pedido | El envío se omite |
| `SECRET_KEY` | Firma de sesiones del panel admin | Usa un valor de desarrollo por defecto |

## Módulos de la app

1. **Autenticación** — Login, registro y "olvidé mi contraseña" (pregunta de seguridad), local + sincronizado con el backend.
2. **Dashboard** — Estadísticas reales según tus reservas (deporte favorito, racha, horas más activas), no datos inventados; estado vacío con llamada a la acción si aún no reservaste nada.
3. **Mapa** — Google Maps, filtros por deporte, búsqueda, ubicación en tiempo real.
4. **Detalle de lugar y reservas** — Galería, reseñas de otros usuarios, favoritos, formulario de reserva con validación de fecha/hora.
5. **Pago único: Yape/Plin** — Se muestra un QR fijo, el usuario sube su comprobante, y el pago queda "pendiente" hasta que un administrador lo aprueba o rechaza (con motivo) desde el panel. No hay reembolso automático — se hace manual y el admin lo marca como reembolsado.
6. **Mis pedidos** — Historial de pagos con su estado (pendiente / aprobado / rechazado / reembolsado); si fue rechazado, se puede volver a subir el comprobante sin crear un pedido duplicado.
7. **Tienda** — Catálogo con filtros por categoría, ficha de producto, carrito, checkout por el mismo flujo de Yape/Plin.
8. **Configuración** — Modo oscuro, GPS, perfil (foto vía Supabase), medallas, favoritos, historial de reservas, suscripción Pro, ayuda y FAQ.
9. **Notificaciones push** — Aviso en la bandeja del sistema cuando se aprueba o rechaza un pago (Firebase Cloud Messaging).

## Datos semilla

Al arrancar el backend por primera vez se crean automáticamente:
- **12 lugares** en Lima
- **9 productos** de tienda
- **3 anuncios** de ejemplo para "Recomendados para ti"
- **1 usuario administrador** (ver credenciales arriba)

## Pendientes conocidos

- Configurar `SUPABASE_URL` / `SUPABASE_SERVICE_KEY` para que las fotos no se guarden en el disco (efímero) del servidor en producción.
- Configurar el envío real de correos (`GMAIL_ADDRESS` / `GMAIL_APP_PASSWORD`).
- Desplegar los cambios del backend a Railway (hoy solo corren en Docker local).
- Conectar el registro de actividades (km recorridos) cuando se termina una ruta en el mapa — hoy esa estadística queda en 0 porque nada la escribe todavía.

---

**Package Android:** `com.tunalex.sportmap` · **Backend:** FastAPI + Postgres
