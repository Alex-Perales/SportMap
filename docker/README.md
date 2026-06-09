# 🐳 Docker Setup - SportMap

Aquí está todo lo necesario para ejecutar la BD de SportMap en Docker.

---

## 🚀 INICIO RÁPIDO (60 segundos)

### Windows (PowerShell)
```powershell
# 1. Abre PowerShell en esta carpeta
cd SportMap\docker

# 2. Ejecuta setup
.\docker-setup.ps1

# ¡Listo! Tu BD está corriendo
```

### Mac/Linux (Terminal)
```bash
# 1. Navega a esta carpeta
cd SportMap/docker

# 2. Ejecuta setup
chmod +x docker-setup.sh
./docker-setup.sh

# ¡Listo! Tu BD está corriendo
```

### Opción Manual
```bash
cp .env.example .env
docker-compose up -d
```

---

## 📡 CONECTAR DESDE TU APP

### Desde Android Emulador
```kotlin
// En tu app Android
val host = "10.0.2.2"        // "Localhost" para emulador
val port = 5432
val database = "sportmap_db"
val user = "alex"
val password = "121416"

val connectionString = "jdbc:postgresql://$host:$port/$database"
```

### Desde Dispositivo Físico
```kotlin
val host = "192.168.1.100"   // Reemplaza con tu IP real
val port = 5432
val database = "sportmap_db"
val user = "alex"
val password = "121416"

val connectionString = "jdbc:postgresql://$host:$port/$database"
```

---

## 🎛️ INTERFACES WEB

### pgAdmin (Administrar BD Visualmente)
```
URL: http://localhost:5050
Email: alex@gmail.com
Password: 121416
```

**Ver datos:**
1. Abre http://localhost:5050
2. Inicia sesión
3. Servers → New Server
4. Nombre: SportMap
5. Host: sportmap-db
6. Usuario: alex
7. Password: 121416

---

## 📂 ARCHIVOS EN ESTA CARPETA

| Archivo | Propósito |
|---------|----------|
| `docker-compose.yml` | 🔧 Configuración de servicios |
| `.env.example` | 🔐 Variables de entorno |
| `init/01-init.sql` | 📊 Script de creación de BD |
| `DOCKER_GUIDE.md` | 📖 Guía completa |
| `docker-setup.ps1` | ⚙️ Script setup Windows |
| `docker-setup.sh` | ⚙️ Script setup Mac/Linux |
| `docker-control.sh` | 🛠️ Helper de comandos (Linux/Mac) |

---

## 🐳 SERVICIOS LEVANTADOS

```
sportmap-db       → PostgreSQL (puerto 5432)
pgadmin           → Interfaz web (puerto 5050)
sportmap-redis    → Redis cache (puerto 6379)
```

---

## 🔨 COMANDOS ÚTILES

```bash
# Ver estado
docker-compose ps

# Ver logs en vivo
docker-compose logs -f

# Conectar a BD
docker-compose exec sportmap-db psql -U alex -d sportmap_db

# Detener
docker-compose down

# Reiniciar
docker-compose restart

# Limpiar todo
docker-compose down -v
```

---

## 📖 LEER PRIMERO

👉 [DOCKER_GUIDE.md](DOCKER_GUIDE.md) — Guía completa con explicaciones

---

## ✅ CHECKLIST

- [ ] Docker instalado
- [ ] Ejecutaste docker-setup.ps1 (o .sh)
- [ ] `docker-compose ps` muestra 3 servicios UP
- [ ] Abriste http://localhost:5050
- [ ] Verificaste credenciales en `docker-compose.yml`

---

## 🆘 ¿Problemas?

```bash
# Ver logs detallados
docker-compose logs sportmap-db

# Reintentar
docker-compose restart

# Reconstruir desde cero
docker-compose down -v
docker-compose up --build -d
```

Para más ayuda, ver [DOCKER_GUIDE.md](DOCKER_GUIDE.md#-solución-de-problemas)

---

**¡Tu BD está lista! 🚀**
