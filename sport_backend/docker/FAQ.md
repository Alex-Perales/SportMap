# ❓ Preguntas Frecuentes - Docker + SportMap

## 🚀 INICIO Y CONFIGURACIÓN

### P: ¿Necesito instalar PostgreSQL en mi máquina?
**R:** No. Docker ya lo trae. Solo necesitas Docker Desktop.

---

### P: ¿Puedo usar Docker en Windows?
**R:** Sí. Necesitas Docker Desktop que ya incluye WSL2 (Windows Subsystem for Linux).

**Opción 1: Docker Desktop (Recomendado)**
- Descarga: https://www.docker.com/products/docker-desktop
- Incluye todo: Docker Engine, Docker Compose, Linux kernel

**Opción 2: Docker CLI solo**
```powershell
choco install docker-cli  # Si tienes Chocolatey
# O descargalo manualmente
```

---

### P: ¿Por qué los scripts (.ps1 y .sh) dan error de "Permission denied"?

**En Windows PowerShell:**
```powershell
# El script está bloqueado
# Solución:

# Opción 1: Desbloquear archivo
Unblock-File -Path .\docker-setup.ps1
.\docker-setup.ps1

# Opción 2: Ejecutar sin guardar
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
.\docker-setup.ps1

# Opción 3: Ejecutar manualmente
docker-compose up -d
```

**En Mac/Linux:**
```bash
# Faltan permisos de ejecución
chmod +x docker-setup.sh
./docker-setup.sh

# O ejecutar directamente con bash:
bash docker-setup.sh
```

---

### P: ¿Cómo configuro IP para dispositivo físico?

```kotlin
// 1. Obtén tu IP real:

// Windows PowerShell:
ipconfig
// Busca: IPv4 Address: 192.168.x.x

// Mac/Linux Terminal:
ifconfig
// Busca: inet 192.168.x.x

// 2. Usa en tu app:
val host = "192.168.1.100"  // Tu IP
val port = 5432
val url = "jdbc:postgresql://$host:$port/sportmap_db"

// 3. El dispositivo debe estar en MISMA RED (WiFi)
```

---

## 🐳 DOCKER BÁSICO

### P: ¿Qué significa `docker-compose up -d`?

```
docker-compose     → Herramienta que orquesta múltiples contenedores
up                 → Crear y levantar
-d                 → Detached (background, sin bloquear terminal)

Equivalente con más pasos:
docker-compose create
docker-compose start
```

---

### P: ¿Diferencia entre `docker-compose stop` y `docker-compose down`?

| Comando | Contenedores | Volúmenes | Red | Datos |
|---------|---|---|---|---|
| `stop` | Parado | Mantiene | Mantiene | ✓ Seguro |
| `down` | Eliminado | Mantiene | Eliminada | ✓ Seguro |
| `down -v` | Eliminado | **ELIMINADO** | Eliminada | ❌ PERDIDOS |

**Recomendación:**
```bash
# Pausa diaria
docker-compose stop

# Limpiar sin perder datos
docker-compose down

# NUNCA sin verificar
docker-compose down -v  # ⚠️ CUIDADO!
```

---

### P: ¿Dónde se guardan mis datos?

```bash
# Ver ubicación exacta
docker inspect sportmap_data | grep Mountpoint

# En Windows, busca en:
\\wsl$\docker-desktop-data\mnt\docker-desktop\volumes\sportmap_data\_data

# En Mac:
/var/lib/docker/volumes/sportmap_data/_data

# En Linux:
/var/lib/docker/volumes/sportmap_data/_data
```

Esos datos persisten incluso si eliminas contenedores (sin `-v`).

---

## 🗄️ POSTGRESQL

### P: ¿Olvidé la contraseña de PostgreSQL?

No hay contraseña "restaurable". Tienes que:

```bash
# Opción 1: Eliminar y recrear (pierde datos)
docker-compose down -v
docker-compose up -d

# Opción 2: Editar .env y recrear
# Cambiar POSTGRES_PASSWORD en .env
# Luego:
docker-compose down -v
cp .env.example .env
# Editar .env con nueva contraseña
docker-compose up -d
```

**Mejor:** Guarda un `.env` seguro en otra ubicación.

---

### P: ¿Cómo conecto desde mi app Android?

**Emulador:**
```kotlin
val connectionString = "jdbc:postgresql://10.0.2.2:5432/sportmap_db"
val user = "alex"
val password = "121416"
```

**Dispositivo Físico:**
```kotlin
val myIp = "192.168.1.100"  // Cambiar a tu IP
val connectionString = "jdbc:postgresql://$myIp:5432/sportmap_db"
val user = "alex"
val password = "121416"
```

---

### P: ¿Por qué la conexión es lenta desde Android?

```
Posibles causas:
1. Red lenta (WiFi débil)
2. Firewall bloqueando puerto 5432
3. Docker corriendo en recurso limitado
4. BD muy grande (índices faltantes)

Soluciones:
1. Verificar velocidad WiFi
2. Abrir puerto 5432 en firewall
3. Aumentar recursos Docker (Settings)
4. Crear índices en tablas importantes

# Ver recursos usados
docker stats
```

---

### P: ¿Cómo hago un backup de la BD?

```bash
# Forma 1: Usando docker-compose
docker-compose exec -T sportmap-db pg_dump \
  -U alex \
  sportmap_db > backup_$(date +%Y%m%d_%H%M%S).sql

# Forma 2: Usando script (si lo hiciste)
./docker-control.sh backup

# El archivo .sql se guardará en el directorio actual
# Puedes compartirlo, versionarlo con Git, etc.
```

---

### P: ¿Cómo restauro un backup?

```bash
# Forma 1: Manual
docker-compose exec -T sportmap-db psql \
  -U alex \
  sportmap_db < backup_20260609_120000.sql

# Forma 2: Script
./docker-control.sh restore backup_20260609_120000.sql

# ⚠️ Esto REEMPLAZA los datos actuales
```

---

### P: ¿Cómo veo las tablas y datos?

```bash
# Opción 1: Terminal SQL
docker-compose exec sportmap-db psql -U alex -d sportmap_db

# Dentro del psql:
\dt                    # Listar tablas
SELECT * FROM users;   # Ver datos
\q                     # Salir

# Opción 2: pgAdmin (web)
# Abre http://localhost:5050
# Email: alex@gmail.com
# Password: 121416
# Luego: Tools → Query Tool → Escribe SQL
```

---

## 🔧 TROUBLESHOOTING ESPECÍFICO

### P: "PostgreSQL connection refused"

```bash
# Paso 1: Ver si está corriendo
docker-compose ps

# Output esperado:
# NAME                STATUS
# sportmap-db         Up (healthy)

# Si Status = "Up (unhealthy)" o "Exited":

# Paso 2: Ver logs
docker-compose logs sportmap-db

# Paso 3: Reintentar esperar
docker-compose restart sportmap-db
sleep 10
docker-compose ps

# Paso 4: Reconstruir si falla
docker-compose down -v
docker-compose up --build -d
```

---

### P: "Port 5432 already in use"

```bash
# Verificar qué usa el puerto

# Windows:
netstat -ano | findstr 5432
# Busca PID, luego:
taskkill /PID <PID> /F

# Mac/Linux:
lsof -i :5432
kill -9 <PID>

# O cambiar puerto en docker-compose.yml:
# ports:
#   - "5433:5432"  # Ahora usa 5433
```

---

### P: "docker-compose: command not found"

```bash
# Docker Compose no está instalado

# Windows: Descarga Docker Desktop (incluye compose)
# https://www.docker.com/products/docker-desktop

# Mac: Igual, descarga Docker Desktop
# O con Homebrew:
brew install docker-compose

# Linux: Instalar manualmente
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verificar:
docker-compose --version
```

---

### P: "Cannot connect to Docker daemon"

```bash
# Docker no está corriendo

# Windows/Mac: Abre Docker Desktop
# Linux: Inicia el servicio
sudo systemctl start docker

# Verificar:
docker ps
```

---

### P: "dial unix /var/run/docker.sock: permission denied"

```bash
# Linux: Sin permisos para Docker

# Solución 1: Usar sudo (temporal)
sudo docker-compose up -d

# Solución 2: Agregar usuario al grupo docker (permanente)
sudo usermod -aG docker $USER
newgrp docker

# Verificar:
docker ps
```

---

### P: Contenedor se detiene inmediatamente

```bash
# Ver por qué falló
docker-compose logs sportmap-db

# Posibles causas:
# 1. Puerto en uso
#    → Cambiar puerto en docker-compose.yml

# 2. Volumen corrupto
#    → docker-compose down -v
#       docker-compose up --build -d

# 3. Archivo SQL con error
#    → Revisar init/01-init.sql

# 4. Insuficientes recursos
#    → Aumentar RAM/CPU en Docker Desktop
```

---

## 💾 DATOS Y VOLÚMENES

### P: ¿Qué pasa si elimino un volumen accidentalmente?

```
docker-compose down -v
# ↓
# TODOS los datos se eliminan
# ↓
# No hay forma de recuperar (sin backup)
```

**Prevención:**
```bash
# Hacer backup ANTES de bajar con -v
docker-compose exec -T sportmap-db pg_dump \
  -U alex \
  sportmap_db > backup_SEGURO.sql

# Luego sí:
docker-compose down -v
```

---

### P: ¿Puedo mover un volumen a otro lugar?

```bash
# Opción 1: Backup y restore
docker-compose exec -T sportmap-db pg_dump -U alex sportmap_db > backup.sql
docker-compose down -v
# Cambiar en docker-compose.yml los volumes paths
docker-compose up -d
docker-compose exec -T sportmap-db psql -U alex sportmap_db < backup.sql

# Opción 2: Copiar volumen directamente (avanzado)
docker volume create new_location
docker run --rm \
  -v old_volume:/from \
  -v new_location:/to \
  alpine \
  cp -r /from /to
```

---

## 🌐 NETWORKING

### P: ¿Por qué puedo conectar a 10.0.2.2 desde emulador?

```
Emulador Android = Máquina virtual Linux
10.0.2.2 = Gateway especial que apunta a HOST
localhost = Red del emulador (no funciona)

Si quieres conectar a tu máquina:
- Usa: 10.0.2.2
- NO: localhost
- NO: 127.0.0.1
```

---

### P: ¿Cómo conectan entre sí los contenedores?

```
Red: sportmap-network

sportmap-db (hostname: sportmap-db)
    ↓
Otros contenedores usan: sportmap-db:5432
    ↓
DNS interno resuelve a IP del contenedor

Ejemplo en pgAdmin:
Host: sportmap-db  (NO localhost)
Port: 5432
```

---

## ⚙️ PERFORMANCE Y RECURSOS

### P: ¿Cuántos recursos necesita Docker?

**Mínimo:**
- CPU: 1 core
- RAM: 2 GB total
- Disco: 10 GB libre

**Recomendado:**
- CPU: 2+ cores
- RAM: 4+ GB
- Disco: 20 GB libre

**Ver recursos:**
```bash
docker stats
docker inspect sportmap-db --format='{{json .HostConfig.Resources}}' | jq
```

---

### P: ¿Cómo aumento recursos de Docker?

**Windows/Mac:**
1. Abre Docker Desktop
2. Settings → Resources
3. Aumenta CPU y Memory
4. Click "Apply & Restart"

**Linux:**
Docker usa recursos del sistema automáticamente.

---

## 🔐 SEGURIDAD

### P: ¿Es seguro guardar contraseñas en .env?

No en producción. Para desarrollo está bien.

**Mejor en Producción:**
```
1. Docker Secrets (Swarm mode)
2. HashiCorp Vault
3. AWS Secrets Manager
4. Azure Key Vault
5. Variables de entorno seguras en CI/CD
```

Para desarrollo:
```bash
# .env (gitignore)
POSTGRES_PASSWORD=121416

# En .gitignore:
.env
.env.local
```

---

### P: ¿Puedo exponer PostgreSQL al internet?

**NO. Nunca.**

```bash
# ❌ MALO - Expone a internet
ports:
  - "5432:5432"
# El puerto 5432 es público

# ✓ BUENO - Solo local
# (Sin ports, solo para otros contenedores)

# Si necesitas acceso remoto:
# 1. Usa VPN
# 2. Usa reverse proxy (nginx)
# 3. Usa API REST (intermediario)
# NO expongas PostgreSQL directamente
```

---

## 📚 PRÓXIMOS PASOS

### Después de levantar Docker:

1. **Verificar que funciona:**
   ```bash
   docker-compose ps
   ```

2. **Ver datos iniciales:**
   ```bash
   docker-compose exec sportmap-db psql -U alex -d sportmap_db -c "SELECT * FROM users;"
   ```

3. **Acceder a pgAdmin:**
   - http://localhost:5050
   - Admin credentials en el navegador

4. **Conectar desde App:**
   - Emulador: 10.0.2.2:5432
   - Device: tu_ip:5432

5. **Hacer backup:**
   ```bash
   docker-compose exec -T sportmap-db pg_dump -U alex sportmap_db > backup.sql
   ```

---

## 🆘 ¿Aún necesitas ayuda?

```bash
# Recopila información útil:
docker-compose logs > logs.txt
docker-compose ps > status.txt
docker version > version.txt
docker-compose config > config.txt

# Ahora tendrás contexto completo para el debugging
```

---

**¡Ahora estás preparado! 🚀**
