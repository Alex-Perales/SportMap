# 🐳 Guía Completa de Docker - SportMap

## 📚 Tabla de Contenidos

1. [Introducción](#introducción)
2. [Requisitos](#requisitos)
3. [Levantar la BD](#levantar-la-bd)
4. [Conexiones](#conexiones)
5. [Comandos Comunes](#comandos-comunes)
6. [Solución de Problemas](#solución-de-problemas)
7. [Arquitectura Docker](#arquitectura-docker)

---

## 🎯 Introducción

Docker te permite ejecutar PostgreSQL y otros servicios en **contenedores aislados** sin instalar nada en tu máquina. Es como una máquina virtual ligera.

### ¿Por qué usar Docker?

✅ No necesitas instalar PostgreSQL directamente  
✅ Mismo entorno en todos los equipos (dev, test, prod)  
✅ Fácil de levantar y limpiar  
✅ Incluye pgAdmin para ver datos visualmente  
✅ Incluye Redis para cache  

---

## ⚙️ Requisitos

### 1. Docker Desktop
- **Windows:** [Descargar Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Mac:** [Descargar Docker Desktop](https://www.docker.com/products/docker-desktop)
- **Linux:** Instalar con `sudo apt install docker.io docker-compose`

### 2. Verificar instalación

```bash
# Abrir terminal/PowerShell y ejecutar:
docker --version
docker-compose --version
```

Deberías ver algo como:
```
Docker version 20.10.x
Docker Compose version 2.x.x
```

---

## 🚀 Levantar la BD

### Windows (PowerShell)

```powershell
# 1. Navegar a la carpeta docker
cd SportMap\docker

# 2. Ejecutar setup
.\docker-setup.ps1

# El script:
# - Copia .env.example a .env
# - Levanta los contenedores
# - Verifica que PostgreSQL esté listo
```

### Mac/Linux (Bash)

```bash
# 1. Navegar a la carpeta docker
cd SportMap/docker

# 2. Dar permisos de ejecución
chmod +x docker-setup.sh

# 3. Ejecutar setup
./docker-setup.sh
```

### Opción Manual (Todos)

```bash
# 1. Ir a la carpeta
cd SportMap/docker

# 2. Copiar archivo de configuración
cp .env.example .env

# 3. Levantar contenedores
docker-compose up -d

# 4. Esperar 5-10 segundos y verificar
docker-compose ps
```

---

## 📡 Conexiones

### PostgreSQL

**Desde tu App Android:**

En `build.gradle.kts` o en tu configuración de Room:

```kotlin
// Host: localhost:5432
// Usuario: alex
// Contraseña: 121416
// Database: sportmap_db

// String de conexión JDBC:
// jdbc:postgresql://localhost:5432/sportmap_db
```

**Desde terminal/herramienta SQL:**

```bash
# Conectar directamente
psql -h localhost -U alex -d sportmap_db
# Password: 121416
```

### pgAdmin (Interfaz Visual)

```
URL: http://localhost:5050
Email: alex@gmail.com
Password: 121416
```

**Cómo conectar tu servidor:**

1. Abre http://localhost:5050 en navegador
2. Inicia sesión con email/password
3. Click en "Add New Server"
4. Nombre: `SportMap PostgreSQL`
5. Host: `sportmap-db`
6. Username: `alex`
7. Password: `121416`
8. Click "Save"

### Redis (Cache)

```
Host: localhost
Port: 6379
```

---

## 🛠️ Comandos Comunes

### Ver estado

```bash
cd docker

# Ver qué contenedores están corriendo
docker-compose ps
```

Salida esperada:
```
CONTAINER ID   IMAGE                       STATUS
abc123...      postgres:15-alpine          Up 2 minutes (healthy)
def456...      dpage/pgadmin4:latest       Up 2 minutes
ghi789...      redis:7-alpine              Up 2 minutes (healthy)
```

### Ver logs

```bash
# Todos los logs en vivo
docker-compose logs -f

# Solo de PostgreSQL
docker-compose logs -f sportmap-db

# Último error
docker-compose logs sportmap-db | tail -20
```

### Entrar a la BD

```bash
# Abrir psql interactivo
docker-compose exec sportmap-db psql -U alex -d sportmap_db

# Ahora puedes escribir SQL:
# SELECT * FROM users;
# \dt (listar tablas)
# \q (salir)
```

### Detener contenedores

```bash
# Pausar pero mantener datos
docker-compose stop

# Eliminar contenedores pero mantener volúmenes
docker-compose down

# Eliminar TODO (cuidado! se pierden datos)
docker-compose down -v
```

### Reiniciar

```bash
# Reiniciar todos
docker-compose restart

# Reconstruir desde cero
docker-compose down
docker-compose up --build -d
```

### Hacer backup

```bash
# Exportar BD a archivo
docker-compose exec -T sportmap-db pg_dump -U alex sportmap_db > backup.sql

# Restaurar desde archivo
docker-compose exec -T sportmap-db psql -U alex sportmap_db < backup.sql
```

### Limpiar completamente

```bash
# Eliminar contenedores, volúmenes, redes
docker-compose down -v
docker system prune -a --volumes
```

---

## 📋 Script de Control (docker-control.sh)

Si usas Linux/Mac, hay un script helper:

```bash
chmod +x docker-control.sh

# Ver ayuda
./docker-control.sh help

# Levantar
./docker-control.sh up

# Ver logs
./docker-control.sh logs
./docker-control.sh logs sportmap-db

# Conectar a BD
./docker-control.sh psql

# Ver salud
./docker-control.sh health

# Crear backup
./docker-control.sh backup

# Restaurar backup
./docker-control.sh restore backup_20260609_101530.sql
```

---

## 🏗️ Arquitectura Docker

### docker-compose.yml

Define 3 servicios:

```yaml
services:
  sportmap-db:      # PostgreSQL en puerto 5432
  pgadmin:          # Web UI en puerto 5050
  sportmap-redis:   # Redis en puerto 6379
```

### Volúmenes (Almacenamiento Persistente)

```
sportmap_data/   ← Datos de PostgreSQL
pgadmin_data/    ← Configuración de pgAdmin
redis_data/      ← Datos de Redis
```

Los datos se guardan en tu máquina, no en el contenedor.

### Redes

```
sportmap-network
  ├── sportmap-db (hostname: sportmap-db)
  ├── pgadmin
  └── sportmap-redis
```

Los contenedores pueden comunicarse entre sí por nombre.

---

## 🔧 Solución de Problemas

### "Permission denied" en Linux

```bash
# Solución: Agregar usuario a grupo docker
sudo usermod -aG docker $USER
newgrp docker

# O usar sudo
sudo docker-compose up -d
```

### "Port already in use"

```bash
# Si el puerto 5432 ya está en uso
# Opción 1: Cambiar puerto en docker-compose.yml
# ports:
#   - "5433:5432"  (usar 5433 en lugar de 5432)

# Opción 2: Matar el proceso actual
# Windows:
netstat -ano | findstr :5432
taskkill /PID <PID> /F

# Linux/Mac:
lsof -i :5432
kill -9 <PID>
```

### "Cannot connect to Docker daemon"

```bash
# Windows: Abrir Docker Desktop
# Mac: Abrir Docker Desktop
# Linux: Iniciar daemon
sudo systemctl start docker

# O, verificar que está corriendo:
docker ps
```

### PostgreSQL no responde

```bash
# Ver logs detallados
docker-compose logs sportmap-db

# Reintentar
docker-compose restart sportmap-db

# Si sigue fallando, reconstruir
docker-compose down -v
docker-compose up --build -d
```

### Conectar desde Android Studio

```kotlin
// En lugar de localhost, usar:
// 10.0.2.2 en emulador Android
// 192.168.x.x en dispositivo real (IP de tu máquina)

val dbUrl = "jdbc:postgresql://10.0.2.2:5432/sportmap_db"  // Emulador
val dbUrl = "jdbc:postgresql://192.168.1.100:5432/sportmap_db"  // Device
```

---

## 📊 Variables de Entorno

En `.env`:

```env
# Base de datos
POSTGRES_DB=sportmap_db
POSTGRES_USER=alex
POSTGRES_PASSWORD=121416

# pgAdmin
PGADMIN_DEFAULT_EMAIL=alex@gmail.com
PGADMIN_DEFAULT_PASSWORD=121416

# Redis
REDIS_HOST=sportmap-redis
REDIS_PORT=6379
```

Cambiar si necesitas credenciales diferentes.

---

## 🎯 Flujo de Trabajo

### Desarrollo Diario

```bash
# Mañana: Levantar servicios
docker-compose up -d

# Trabajo: Usar BD normalmente

# Mediodía: Ver logs si hay problema
docker-compose logs -f

# Tarde: Hacer cambios en la BD
docker-compose exec sportmap-db psql -U alex sportmap_db

# Noche: Hacer backup
docker-compose exec -T sportmap-db pg_dump -U alex sportmap_db > daily_backup.sql

# Fin de semana: Pausar servicios
docker-compose stop
```

### Cuando tienes problema

```bash
1. Ver logs:       docker-compose logs sportmap-db
2. Verificar salud: docker-compose ps
3. Reconectar:     docker-compose restart
4. Reconstruir:    docker-compose down -v && docker-compose up --build -d
```

---

## 📚 Recursos Útiles

- [Docker Docs](https://docs.docker.com)
- [PostgreSQL en Docker](https://hub.docker.com/_/postgres)
- [pgAdmin en Docker](https://hub.docker.com/r/dpage/pgadmin4)
- [Redis en Docker](https://hub.docker.com/_/redis)

---

## ✅ Checklist

- [x] Docker instalado
- [x] docker-compose.yml configurado
- [x] init/01-init.sql con todas las tablas
- [x] .env.example con variables
- [x] Scripts de setup (Windows/Mac/Linux)
- [x] pgAdmin para administración visual
- [x] Redis para cache
- [x] Volúmenes persistentes

---

**¡Ahora tu BD corre en Docker! 🐳**

Próximo paso: Conectar desde tu app Android →

