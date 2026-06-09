# 📖 Índice de Documentación Docker - SportMap

Bienvenido a la documentación completa de Docker para SportMap. Aquí encontrarás todo lo que necesitas.

---

## 🚀 COMIENZA AQUÍ

### Primeros Pasos (5 minutos)

**Si es tu primera vez:**
1. Leer: [README.md](README.md) ← EMPIEZA AQUÍ
2. Ejecutar: `.\docker-setup.ps1` (Windows) o `./docker-setup.sh` (Mac/Linux)
3. Verificar: `docker-compose ps`
4. ¡Listo!

---

## 📚 DOCUMENTOS POR TEMA

### 🎯 Inicio Rápido
- **[README.md](README.md)** — Inicio en 60 segundos
  - Levantar docker-compose
  - Servicios disponibles
  - Credenciales básicas

---

### 🐳 Entendimiento de Docker
- **[ARCHITECTURE.md](ARCHITECTURE.md)** — Cómo funciona Docker
  - Diagrama de contenedores
  - Flujo de datos
  - Volúmenes y persistencia
  - Networking
  - Ciclo de vida

- **[QUICK_COMMANDS.md](QUICK_COMMANDS.md)** — Comandos frecuentes
  - Levantar/detener
  - Ver logs
  - Conectar a BD
  - Backups
  - Troubleshooting rápido

---

### 📖 Guías Completas
- **[DOCKER_GUIDE.md](DOCKER_GUIDE.md)** — Manual comprensivo
  - Requisitos
  - Instalación en Windows/Mac/Linux
  - Conexiones
  - Comandos comunes
  - Solución de problemas
  - Flujo de trabajo

- **[ANDROID_CONNECTION.md](ANDROID_CONNECTION.md)** — Conectar tu app
  - JDBC directo
  - Room + Retrofit
  - Hybrid (recomendado)
  - Ejemplos de código
  - Configuración por dispositivo

---

### ❓ Preguntas Frecuentes
- **[FAQ.md](FAQ.md)** — Respuestas a preguntas comunes
  - Inicio y configuración
  - Docker básico
  - PostgreSQL
  - Troubleshooting
  - Datos y volúmenes
  - Networking
  - Performance

---

### 📋 Configuración
- **[.env.example](.env.example)** — Variables de entorno
  - Credenciales PostgreSQL
  - Credenciales pgAdmin
  - Configuración Redis
  - URL de conexión

- **[docker-compose.yml](docker-compose.yml)** — Configuración de servicios
  - PostgreSQL
  - pgAdmin
  - Redis
  - Volúmenes
  - Redes

---

### 📊 Base de Datos
- **[init/01-init.sql](init/01-init.sql)** — Script de inicialización
  - 15 tablas
  - Índices
  - Foreign keys
  - Datos de ejemplo

---

### ⚙️ Scripts
- **[docker-setup.ps1](docker-setup.ps1)** — Instalador Windows
  - Copia .env
  - Levanta contenedores
  - Verifica salud

- **[docker-setup.sh](docker-setup.sh)** — Instalador Mac/Linux
  - Copia .env
  - Levanta contenedores
  - Verifica salud

- **[docker-control.sh](docker-control.sh)** — Herramienta de control (Mac/Linux)
  - Levantar/detener
  - Ver logs
  - Conectar a BD
  - Backups
  - Salud del sistema

---

## 🗺️ MAPA DE TEMAS

```
DOCKER SETUP
├─ PRIMEROS PASOS
│  └─ README.md ← Empieza aquí
│
├─ INSTALACIÓN
│  ├─ DOCKER_GUIDE.md (requisitos + pasos)
│  └─ docker-setup.ps1/.sh (automático)
│
├─ FUNCIONAMIENTO
│  ├─ ARCHITECTURE.md (cómo funciona)
│  └─ docker-compose.yml (configuración)
│
├─ USO DIARIO
│  ├─ QUICK_COMMANDS.md (comandos comunes)
│  ├─ docker-control.sh (helper)
│  └─ FAQ.md (respuestas)
│
├─ CONECTAR APP
│  └─ ANDROID_CONNECTION.md (código ejemplos)
│
└─ DATOS
   ├─ init/01-init.sql (BD)
   └─ .env.example (configuración)
```

---

## 🎓 RUTAS DE APRENDIZAJE

### Ruta 1: Principiante (Primeras 2 horas)
1. Lee [README.md](README.md)
2. Ejecuta `docker-setup.ps1` (Windows) o `docker-setup.sh` (Mac/Linux)
3. Lee [ARCHITECTURE.md](ARCHITECTURE.md) — entender conceptos
4. Lee [QUICK_COMMANDS.md](QUICK_COMMANDS.md) — comandos básicos
5. Abre pgAdmin en http://localhost:5050

### Ruta 2: Intermedio (Siguiente semana)
1. Lee [DOCKER_GUIDE.md](DOCKER_GUIDE.md) — todo detallado
2. Práctica con [QUICK_COMMANDS.md](QUICK_COMMANDS.md)
3. Lee [ANDROID_CONNECTION.md](ANDROID_CONNECTION.md)
4. Conecta tu app Android
5. Consulta [FAQ.md](FAQ.md) cuando tengas dudas

### Ruta 3: Avanzado (Cuando necesites)
1. Edita [docker-compose.yml](docker-compose.yml) para personalizar
2. Modifica [init/01-init.sql](init/01-init.sql) para BD custom
3. Crea scripts propios basados en [docker-control.sh](docker-control.sh)
4. Implementa health checks avanzados
5. Configura backup/restore automático

---

## 🔍 BUSCAR POR TEMA

### Problemas Comunes
- "Permission denied" → [FAQ.md - Permission denied](FAQ.md)
- "Port already in use" → [FAQ.md - Port already in use](FAQ.md)
- "Connection refused" → [FAQ.md - PostgreSQL connection refused](FAQ.md)
- "Cannot connect to Docker daemon" → [FAQ.md - Cannot connect to Docker daemon](FAQ.md)

### Tareas Frecuentes
- Levantar BD → [README.md](README.md) o [QUICK_COMMANDS.md](QUICK_COMMANDS.md#levantar-detener)
- Ver logs → [QUICK_COMMANDS.md - Logs y Debugging](QUICK_COMMANDS.md#logs-y-debugging)
- Conectar desde terminal → [QUICK_COMMANDS.md - Conectar a BD](QUICK_COMMANDS.md#conectar-a-bases-de-datos)
- Hacer backup → [QUICK_COMMANDS.md - Backups](QUICK_COMMANDS.md#-backups-y-restauración)
- Conectar desde app → [ANDROID_CONNECTION.md](ANDROID_CONNECTION.md)

### Conceptos
- ¿Qué es Docker? → [ARCHITECTURE.md - Introducción](ARCHITECTURE.md)
- ¿Cómo funciona networking? → [ARCHITECTURE.md - Red](ARCHITECTURE.md#red-networking)
- ¿Dónde están mis datos? → [ARCHITECTURE.md - Volúmenes](ARCHITECTURE.md#volúmenes-almacenamiento-persistente)
- ¿Qué son volúmenes? → [FAQ.md - ¿Dónde se guardan mis datos?](FAQ.md)
- ¿Diferencia container vs image? → [ARCHITECTURE.md - Container vs Image](ARCHITECTURE.md#diferencia-container-vs-image)

---

## ✅ CHECKLIST

Antes de considerar que está listo:

- [ ] Docker Desktop instalado (`docker --version` muestra versión)
- [ ] `docker-compose` disponible (`docker-compose --version` muestra versión)
- [ ] Archivo `.env` copiado desde `.env.example`
- [ ] `docker-compose up -d` ejecutado exitosamente
- [ ] `docker-compose ps` muestra 3 servicios "UP"
- [ ] Puedo acceder a http://localhost:5050 (pgAdmin)
- [ ] Puedo conectar con credenciales: alex / 121416
- [ ] `docker-compose exec sportmap-db psql -U alex -d sportmap_db` funciona
- [ ] Tengo un backup de BD
- [ ] Mi app Android puede conectar a la BD

---

## 💡 TIPS

1. **Siempre hacer backup antes de `down -v`**
   ```bash
   docker-compose exec -T sportmap-db pg_dump -U alex sportmap_db > backup.sql
   docker-compose down -v  # Ahora es seguro
   ```

2. **Guardar .env seguro**
   - No lo subas a Git
   - Guarda copia en lugar seguro
   - Si lo pierdes, reinicia: `docker-compose down -v`

3. **Logs son tus amigos**
   ```bash
   docker-compose logs -f
   # Muestra exactamente qué está pasando
   ```

4. **Health checks**
   - `docker-compose ps` muestra estado
   - "healthy" = todo bien
   - "unhealthy" = algo falla

5. **Usar script de control**
   ```bash
   chmod +x docker-control.sh
   ./docker-control.sh help
   # Hace las cosas más fácil
   ```

---

## 🆘 AYUDA RÁPIDA

**Si algo no funciona:**

1. Ver logs:
   ```bash
   docker-compose logs -f
   ```

2. Ver estado:
   ```bash
   docker-compose ps
   ```

3. Reintentar:
   ```bash
   docker-compose restart
   ```

4. Reconstruir:
   ```bash
   docker-compose down -v
   docker-compose up --build -d
   ```

5. Consultar [FAQ.md](FAQ.md)

---

## 📞 REFERENCIAS

- [Docker Docs](https://docs.docker.com)
- [Docker Compose Docs](https://docs.docker.com/compose/)
- [PostgreSQL Docs](https://www.postgresql.org/docs/)
- [pgAdmin Docs](https://www.pgadmin.org/docs/)

---

## 📝 VERSIONES

- **Docker Compose:** 3.9
- **PostgreSQL:** 15-alpine
- **pgAdmin:** 4 (latest)
- **Redis:** 7-alpine

---

**¡Bienvenido a Docker! 🐳**

Comienza con [README.md](README.md) →

