# 🐳 Comandos Docker Rápidos - SportMap

## ⚡ COMANDOS ESENCIALES

### Levantar/Detener

```bash
# Levantar contenedores (modo background)
docker-compose up -d

# Ver estado
docker-compose ps

# Detener (pausa, mantiene datos)
docker-compose stop

# Detener y eliminar contenedores (mantiene datos)
docker-compose down

# Eliminar TODO (cuidado! perderás datos)
docker-compose down -v
```

---

### Logs y Debugging

```bash
# Ver todos los logs en vivo
docker-compose logs -f

# Ver logs de PostgreSQL solamente
docker-compose logs -f sportmap-db

# Ver los últimos 50 líneas
docker-compose logs --tail=50

# Ver logs con timestamps
docker-compose logs -f --timestamps
```

---

### Conectar a Bases de Datos

```bash
# Entrar a psql (terminal interactivo)
docker-compose exec sportmap-db psql -U alex -d sportmap_db

# Una vez adentro, comandos útiles:
# \dt                          → Listar tablas
# SELECT * FROM users;         → Consulta SQL
# SELECT COUNT(*) FROM users;  → Contar registros
# \q                           → Salir
```

---

### Ejecutar comandos en contenedor

```bash
# Abrir shell bash en PostgreSQL
docker-compose exec sportmap-db /bin/bash

# Ejecutar un comando directo (sin entrar)
docker-compose exec -T sportmap-db pg_dump -U alex sportmap_db > backup.sql

# Ver estadísticas
docker-compose exec sportmap-db pg_stat_statements
```

---

## 📊 BACKUPS Y RESTAURACIÓN

```bash
# Crear backup (exportar BD a archivo)
docker-compose exec -T sportmap-db pg_dump \
  -U alex \
  sportmap_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Restaurar desde backup
docker-compose exec -T sportmap-db psql \
  -U alex \
  sportmap_db < backup.sql

# Copiar backup a otra ubicación
docker cp sportmap-database:/var/lib/postgresql/data ./backup_folder
```

---

## 🔧 MANTENIMIENTO

```bash
# Reiniciar un servicio específico
docker-compose restart sportmap-db
docker-compose restart pgadmin

# Reconstruir imagen (después de cambios en Dockerfile)
docker-compose build

# Levantar y reconstruir
docker-compose up --build -d

# Ver recursos usados
docker stats

# Limpiar imágenes no usadas
docker image prune -a

# Limpiar volúmenes no usados
docker volume prune
```

---

## 🔍 VERIFICACIÓN DE SALUD

```bash
# Ver si PostgreSQL está respondiendo
docker-compose exec -T sportmap-db pg_isready -U alex

# Ver si Redis está respondiendo
docker-compose exec -T sportmap-redis redis-cli ping

# Verificar puerto abierto
netstat -tuln | grep 5432     # Linux/Mac
netstat -ano | findstr 5432   # Windows

# Ping dentro del contenedor
docker-compose exec sportmap-db ping redis
```

---

## 🗂️ VOLÚMENES Y DATOS

```bash
# Ver volúmenes creados
docker volume ls

# Inspeccionar volumen específico
docker volume inspect sportmap_data

# Punto de montaje real:
# Windows: \\wsl$\docker-desktop-data\mnt\docker-desktop
# Mac: /var/lib/docker/volumes/
# Linux: /var/lib/docker/volumes/

# Copiar datos fuera de contenedor
docker cp sportmap-database:/var/lib/postgresql/data ./my_backup
```

---

## 🚨 TROUBLESHOOTING RÁPIDO

### "Container exits immediately"
```bash
# Ver por qué falló
docker-compose logs sportmap-db

# Intenta reconstruir
docker-compose down -v
docker-compose up --build -d
```

### "Port already in use"
```bash
# Ver qué usa el puerto
lsof -i :5432

# Cambiar puerto en docker-compose.yml:
# ports:
#   - "5433:5432"  (usa 5433 en lugar de 5432)
```

### "Cannot connect to database"
```bash
# Esperar a que PostgreSQL esté listo
sleep 10
docker-compose exec sportmap-db pg_isready -U alex

# Si sigue fallando:
docker-compose restart sportmap-db
```

---

## 📡 NETWORKING

```bash
# Ver redes Docker
docker network ls

# Ver contenedores en red
docker network inspect sportmap-network

# Conectar nuevo contenedor a la red
docker run --network sportmap-network --name test-container alpine

# Ver IP del contenedor
docker inspect sportmap-database | grep IPAddress
```

---

## 🎯 CHEAT SHEET VISUAL

```
┌─────────────────────────────────────────────────┐
│  OPERACIONES MÁS COMUNES                        │
├─────────────────────────────────────────────────┤
│ Levantar       → docker-compose up -d           │
│ Estado         → docker-compose ps              │
│ Logs           → docker-compose logs -f         │
│ Entrar BD      → docker-compose exec ... psql   │
│ Detener        → docker-compose stop            │
│ Eliminar       → docker-compose down            │
│ Limpiar todo   → docker-compose down -v         │
│ Backup         → pg_dump                        │
│ Restaurar      → psql < backup.sql              │
└─────────────────────────────────────────────────┘
```

---

## 📝 SCRIPT DE MONITOREO

```bash
#!/bin/bash
# Monitorear en vivo

watch -n 1 'docker-compose ps && echo "" && docker stats --no-stream'
```

---

## 🔐 CREDENCIALES POR SI LAS OLVIDAS

```
POSTGRES_USER: alex
POSTGRES_PASSWORD: 121416
POSTGRES_DB: sportmap_db

PGADMIN_EMAIL: alex@gmail.com
PGADMIN_PASSWORD: 121416

REDIS: no requiere contraseña por defecto
```

---

## 📚 REFERENCIAS

```bash
# Ayuda general
docker-compose help
docker-compose COMANDO help

# Documentación
man docker-compose
docker-compose version
```

---

## 🆘 CUANDO TODO FALLA

```bash
# Opción nuclear: empezar de cero
docker-compose down -v
docker system prune -a
docker-compose up --build -d

# Ver logs detallados
docker-compose logs --tail=100 sportmap-db

# Verificar instalación
docker version
docker-compose version
docker run hello-world
```

---

**¡Domina Docker! 🚀**
