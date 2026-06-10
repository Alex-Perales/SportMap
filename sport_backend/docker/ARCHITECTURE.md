# 🏗️ Arquitectura Docker - SportMap

## Diagrama General

```
┌──────────────────────────────────────────────────────────────┐
│                    TU MÁQUINA (HOST)                         │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌────────────────────────────────────────────────────────┐  │
│  │         DOCKER (Aislado del resto)                     │  │
│  ├────────────────────────────────────────────────────────┤  │
│  │                                                         │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │  CONTAINER 1: PostgreSQL                        │  │  │
│  │  ├──────────────────────────────────────────────────┤  │  │
│  │  │ OS: Alpine Linux                                │  │  │
│  │  │ Puerto: 5432 (mapeado a 5432 en HOST)          │  │  │
│  │  │ Datos: /var/lib/postgresql/data                │  │  │
│  │  │ Volumen: sportmap_data (en tu máquina)         │  │  │
│  │  │ Estado: RUNNING (saludable ✓)                  │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  │                       │                                  │  │
│  │  ┌────────────────────┴─────────────────────────────┐  │  │
│  │  │  CONTAINER 2: pgAdmin                           │  │  │
│  │  ├──────────────────────────────────────────────────┤  │  │
│  │  │ OS: Linux                                        │  │  │
│  │  │ Puerto: 5050 (acceso web)                       │  │  │
│  │  │ Función: Admin UI para PostgreSQL               │  │  │
│  │  │ Estado: RUNNING (saludable ✓)                   │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  │                       │                                  │  │
│  │  ┌────────────────────┴─────────────────────────────┐  │  │
│  │  │  CONTAINER 3: Redis                             │  │  │
│  │  ├──────────────────────────────────────────────────┤  │  │
│  │  │ OS: Alpine Linux                                │  │  │
│  │  │ Puerto: 6379 (cache)                            │  │  │
│  │  │ Datos: /data (volumen redis_data)              │  │  │
│  │  │ Estado: RUNNING (saludable ✓)                   │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  │                                                         │  │
│  │  ┌──────────────────────────────────────────────────┐  │  │
│  │  │  RED: sportmap-network (bridge)                │  │  │
│  │  │  Permite comunicación entre contenedores        │  │  │
│  │  │  - PostgreSQL → sportmap-db                    │  │  │
│  │  │  - Redis → sportmap-redis                      │  │  │
│  │  │  - pgAdmin → pgadmin                           │  │  │
│  │  └──────────────────────────────────────────────────┘  │  │
│  │                                                         │  │
│  └─────────────────────────────────────────────────────────┘  │
│                                                               │
│  CONEXIONES A TU APP:                                        │
│  - localhost:5432 → PostgreSQL                             │
│  - localhost:5050 → pgAdmin (web)                          │
│  - localhost:6379 → Redis                                  │
│                                                               │
└──────────────────────────────────────────────────────────────┘

                           ↓

┌──────────────────────────────────────────────────────────────┐
│              TU APP ANDROID                                  │
├──────────────────────────────────────────────────────────────┤
│                                                               │
│  Emulador:  usa 10.0.2.2 para conectar a localhost         │
│  Device:    usa 192.168.x.x (tu IP real)                   │
│                                                               │
│  Consultas SQL → PostgreSQL                                │
│  Cache → Redis                                             │
│  Admin → pgAdmin (en navegador)                            │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

---

## Flujo de Datos

```
APP ANDROID (Emulador)
        │
        ├─ "SELECT * FROM users"
        │         ↓
        │   10.0.2.2:5432
        │         ↓
        │   CONTENEDOR PostgreSQL
        │         ↓
        │   volumen sportmap_data
        │         ↓
        │   DATOS EN TU MÁQUINA
        │    (persistentes)
        │
        └─ "Obtener datos de cache"
                  ↓
            localhost:6379
                  ↓
            CONTENEDOR Redis
                  ↓
            Respuesta rápida
```

---

## Volúmenes (Almacenamiento Persistente)

```
TU MÁQUINA
    │
    ├── sportmap_data/           ← Datos PostgreSQL
    │   └── 12/base/
    │       └── 16384/           ← Base de datos
    │
    ├── pgadmin_data/            ← Configuración pgAdmin
    │   └── pgadmin4/
    │
    └── redis_data/              ← Datos Redis
        └── appendonly.aof


CONTENEDORES (Leen/escriben en estos volúmenes)
    │
    ├── sportmap-db
    │   └── /var/lib/postgresql/data → sportmap_data/
    │
    ├── pgadmin
    │   └── /var/lib/pgadmin → pgadmin_data/
    │
    └── sportmap-redis
        └── /data → redis_data/
```

**Importancia:** Si eliminas el volumen con `docker-compose down -v`, 
¡PIERDES TODOS LOS DATOS!

---

## Red (Networking)

```
┌─────────────────────────────────────┐
│  sportmap-network (bridge)          │
├─────────────────────────────────────┤
│                                     │
│  Contenedores conectados:           │
│                                     │
│  ┌──────────────────────────────┐  │
│  │ sportmap-db                  │  │
│  │ IP interna: 172.20.0.2       │  │
│  │ Hostname: sportmap-db        │  │
│  │ Puerto: 5432 (interno)       │  │
│  └──────────────────────────────┘  │
│           │                         │
│           │ (comunicación interna)  │
│           │                         │
│  ┌────────▼──────────────────────┐  │
│  │ pgadmin                       │  │
│  │ IP interna: 172.20.0.3        │  │
│  │ Usa: sportmap-db:5432         │  │
│  │ Puerto: 80 (interno)          │  │
│  └───────────────────────────────┘  │
│           │                         │
│           │ (comunicación interna)  │
│           │                         │
│  ┌────────▼──────────────────────┐  │
│  │ sportmap-redis                │  │
│  │ IP interna: 172.20.0.4        │  │
│  │ Hostname: sportmap-redis      │  │
│  │ Puerto: 6379 (interno)        │  │
│  └───────────────────────────────┘  │
│                                     │
└─────────────────────────────────────┘

Desde afuera (tu app):
- localhost:5432 → sportmap-db
- localhost:5050 → pgadmin
- localhost:6379 → redis
```

---

## Ciclo de Vida de un Contenedor

```
docker-compose up -d
    ↓
┌────────────────────────────────────┐
│ 1. CREAR IMAGEN                    │
│    (descarga postgres:15-alpine)   │
├────────────────────────────────────┤
│ 2. CREAR CONTENEDOR                │
│    (instance de la imagen)         │
├────────────────────────────────────┤
│ 3. EJECUTAR                        │
│    (PostgreSQL inicia)             │
├────────────────────────────────────┤
│ 4. EJECUTAR INIT SCRIPT            │
│    (01-init.sql crea tablas)       │
├────────────────────────────────────┤
│ 5. RUNNING ✓                       │
│    (listo para conexiones)         │
└────────────────────────────────────┘

Cuando haces docker-compose down:
    ↓
┌────────────────────────────────────┐
│ 1. DETENER contenedores            │
│    (SIGTERM → SIGKILL)             │
├────────────────────────────────────┤
│ 2. REMOVER contenedores            │
├────────────────────────────────────┤
│ 3. REMOVER red (si -v)             │
│    docker-compose down -v          │
├────────────────────────────────────┤
│ 4. REMOVER volúmenes (si -v)       │
│    ⚠️ DATOS ELIMINADOS             │
└────────────────────────────────────┘
```

---

## Mapeo de Puertos

```
TU MÁQUINA FÍSICA (HOST)
    ↓
    ├─ :5432 (Port Host)
    │    ↓
    │    Mapeo
    │    ↓
    └─→ CONTENEDOR PostgreSQL
         :5432 (Puerto Interno)

┌─────────────────────────────────┐
│ docker-compose.yml              │
├─────────────────────────────────┤
│ ports:                          │
│   - "5432:5432"                 │
│     ^host  ^container           │
│                                 │
│ Si quieres diferente:           │
│   - "5433:5432"                 │
│     Accesa: localhost:5433      │
│     Internamente: :5432         │
└─────────────────────────────────┘
```

---

## Estados Posibles

```
UP (Saludable)        ✓  Contenedor corriendo bien
UP (Unhealthy)        ⚠️  Contenedor corre pero falla health check
Exited               ❌  Parado (no está corriendo)
Restarting           🔄  Reiniciando automáticamente
Removing             🗑️  Eliminándose
Dead                 💀  Error grave (reinicio fallido)
```

---

## Qué pasa en `docker-compose up -d`

```
1. Lee docker-compose.yml
2. Crea red sportmap-network
3. Crea volúmenes:
   - sportmap_data
   - pgadmin_data
   - redis_data
4. Para cada servicio:
   a) Descarga imagen si no existe
   b) Crea contenedor
   c) Aplica variables de entorno (.env)
   d) Monta volúmenes
   e) Mapea puertos
   f) Conecta a red
   g) Inicia contenedor
5. Si existe init/.sql:
   - Ejecuta scripts de inicialización
6. Contenedores esperan traffic
7. Los logs se guardan
```

---

## Recursos Usados

```
PostgreSQL:  ~150 MB (imagen)
             ~50 MB (datos iniciales)
             
pgAdmin:     ~300 MB (imagen)
             ~20 MB (config)
             
Redis:       ~50 MB (imagen)
             ~10 MB (datos)

TOTAL:       ~600 MB inicial
             Crece con datos
```

---

## Monitoreo en Tiempo Real

```bash
# Ver recursos en vivo
docker stats

# Output:
CONTAINER ID   NAME              CPU %   MEM USAGE   MEM LIMIT
abc123...      sportmap-db       2.3%    45.2MB      500MB
def456...      pgadmin           1.1%    120MB       500MB
ghi789...      sportmap-redis    0.5%    8.2MB       500MB
```

---

## ¿Dónde están mis datos?

```
Windows:
  - Docker Desktop → Settings → Resources → File Sharing
  - O en: \\wsl$\docker-desktop-data\mnt\docker-desktop\volumes

Mac:
  - /var/lib/docker/volumes/

Linux:
  - /var/lib/docker/volumes/

Comando universal:
  docker inspect sportmap_data | grep Mountpoint
```

---

## Diferencia: Container vs Image

```
IMAGE (Plantilla)          CONTAINER (Instancia)
├─ Inmutable               ├─ Mutable
├─ Reutilizable            ├─ De corta vida
├─ ~300 MB (pgadmin)       ├─ + datos volúmenes
├─ Se descarga 1 vez       ├─ Se crea cada vez
└─ En registries remotos   └─ En tu máquina local


Analogía: 
IMAGE = Receta de cocina
CONTAINER = Plato cocinado
```

---

## Troubleshooting Visual

```
¿Funciona?
    │
    ├─ SÍ → ✓ Continúa
    │
    └─ NO
        │
        ├─ ¿Contenedores corriendo?
        │  docker-compose ps
        │  │
        │  ├─ NO → docker-compose up -d
        │  │
        │  └─ SÍ → Ver logs
        │          docker-compose logs
        │          │
        │          ├─ Error de conexión
        │          │  → Esperar más tiempo
        │          │  → Ver puerto correcto
        │          │
        │          └─ Error en script SQL
        │             → Revisar init/01-init.sql
        │
        └─ ¿Puerto disponible?
           netstat -tuln | grep 5432
           │
           ├─ NO → Cambiar puerto
           │       docker-compose.yml
           │
           └─ SÍ → Reconstruir
                   docker-compose down -v
                   docker-compose up --build -d
```

---

**¡Ahora entiendes cómo funciona Docker! 🐳**
