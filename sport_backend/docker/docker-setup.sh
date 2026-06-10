#!/bin/bash

# ===================================
# DOCKER SETUP - Linux/Mac
# ===================================

echo "Configurando Docker para SportMap..."
echo ""

# Copiar .env
if [ -f ".env" ]; then
    echo "✓ .env ya existe"
else
    cp .env.example .env
    echo "✓ .env creado desde .env.example"
fi

echo ""
echo "Levantando contenedores Docker..."
docker-compose up -d

echo ""
echo "Esperando a que PostgreSQL esté listo..."
sleep 5

# Verificar conexión
echo "Verificando conexión..."
for i in {1..30}; do
    if docker-compose exec -T sportmap-db pg_isready -U alex >/dev/null 2>&1; then
        echo "✓ PostgreSQL está listo"
        
        # Mostrar información
        echo ""
        echo "==================================="
        echo "   INFORMACIÓN DE CONEXIÓN"
        echo "==================================="
        echo ""
        echo "PostgreSQL:"
        echo "  Host: localhost"
        echo "  Port: 5432"
        echo "  User: alex"
        echo "  Password: 121416"
        echo "  Database: sportmap_db"
        echo ""
        echo "pgAdmin (Interfaz de BD):"
        echo "  URL: http://localhost:5050"
        echo "  Email: alex@gmail.com"
        echo "  Password: 121416"
        echo ""
        echo "Redis (Cache):"
        echo "  Host: localhost"
        echo "  Port: 6379"
        echo ""
        echo "==================================="
        exit 0
    fi
    sleep 1
done

echo "✗ PostgreSQL no respondió a tiempo"
echo "Intenta: docker-compose logs sportmap-db"
exit 1
