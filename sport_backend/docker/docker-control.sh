#!/bin/bash

# ===================================
# SCRIPT DE CONTROL DOCKER
# Uso: ./docker-control.sh [comando]
# ===================================

set -e

# Colores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Funciones
print_header() {
    echo -e "${BLUE}===================================${NC}"
    echo -e "${BLUE}$1${NC}"
    echo -e "${BLUE}===================================${NC}"
}

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

# Comando: up
cmd_up() {
    print_header "LEVANTANDO CONTENEDORES"
    docker-compose up -d
    
    print_success "Contenedores levantados"
    sleep 5
    
    print_header "INFORMACIÓN DE CONEXIÓN"
    echo -e "PostgreSQL:"
    echo "  Host: localhost"
    echo "  Port: 5432"
    echo "  User: alex"
    echo "  Password: 121416"
    echo "  Database: sportmap_db"
    echo ""
    echo -e "pgAdmin (Admin de BD):"
    echo "  URL: http://localhost:5050"
    echo "  Email: alex@gmail.com"
    echo "  Password: 121416"
    echo ""
    echo -e "Redis (Cache):"
    echo "  Host: localhost"
    echo "  Port: 6379"
}

# Comando: down
cmd_down() {
    print_header "DETENIENDO CONTENEDORES"
    docker-compose down
    print_success "Contenedores detenidos"
}

# Comando: status
cmd_status() {
    print_header "ESTADO DE CONTENEDORES"
    docker-compose ps
}

# Comando: logs
cmd_logs() {
    print_header "LOGS EN VIVO"
    echo "Presiona Ctrl+C para salir"
    sleep 2
    docker-compose logs -f
}

# Comando: logs de servicio específico
cmd_logs_service() {
    if [ -z "$2" ]; then
        print_error "Especifica un servicio (db, pgadmin, redis)"
        echo "Servicios disponibles:"
        docker-compose ps --services
        exit 1
    fi
    print_header "LOGS: $2"
    docker-compose logs -f "$2"
}

# Comando: psql (conectar a BD)
cmd_psql() {
    print_header "CONECTANDO A PostgreSQL"
    docker-compose exec sportmap-db psql -U alex -d sportmap_db
}

# Comando: shell en contenedor
cmd_shell() {
    SERVICE=${2:-sportmap-db}
    print_header "SHELL EN: $SERVICE"
    docker-compose exec "$SERVICE" /bin/sh
}

# Comando: restart
cmd_restart() {
    print_header "REINICIANDO CONTENEDORES"
    docker-compose restart
    print_success "Contenedores reiniciados"
}

# Comando: rebuild
cmd_rebuild() {
    print_header "RECONSTRUYENDO CONTENEDORES"
    docker-compose down
    docker-compose up --build -d
    print_success "Contenedores reconstruidos y levantados"
}

# Comando: clean (eliminar todo)
cmd_clean() {
    print_warning "Esto eliminará TODOS los datos y contenedores"
    read -p "¿Estás seguro? (s/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Ss]$ ]]; then
        print_header "LIMPIANDO"
        docker-compose down -v
        print_success "Contenedores y volúmenes eliminados"
    else
        print_warning "Operación cancelada"
    fi
}

# Comando: health (verificar salud)
cmd_health() {
    print_header "VERIFICANDO SALUD DE SERVICIOS"
    
    echo "Verificando PostgreSQL..."
    if docker-compose exec -T sportmap-db pg_isready -U alex >/dev/null 2>&1; then
        print_success "PostgreSQL está saludable"
    else
        print_error "PostgreSQL no está respondiendo"
    fi
    
    echo ""
    echo "Verificando Redis..."
    if docker-compose exec -T sportmap-redis redis-cli ping >/dev/null 2>&1; then
        print_success "Redis está saludable"
    else
        print_error "Redis no está respondiendo"
    fi
}

# Comando: backup
cmd_backup() {
    print_header "CREANDO BACKUP DE BD"
    BACKUP_FILE="backup_$(date +%Y%m%d_%H%M%S).sql"
    docker-compose exec -T sportmap-db pg_dump -U alex sportmap_db > "../backups/$BACKUP_FILE"
    print_success "Backup creado: $BACKUP_FILE"
}

# Comando: restore
cmd_restore() {
    if [ -z "$2" ]; then
        print_error "Especifica archivo de backup"
        exit 1
    fi
    print_header "RESTAURANDO BD"
    docker-compose exec -T sportmap-db psql -U alex sportmap_db < "$2"
    print_success "BD restaurada desde: $2"
}

# Help
cmd_help() {
    cat << EOF
${BLUE}DOCKER CONTROL - SportMap${NC}

${GREEN}Uso:${NC}
  ./docker-control.sh [comando] [opciones]

${GREEN}Comandos:${NC}
  up                 - Levantar todos los contenedores
  down               - Detener todos los contenedores
  status             - Ver estado de contenedores
  logs               - Ver logs en vivo (todos)
  logs [servicio]    - Ver logs de un servicio
  psql               - Conectar a PostgreSQL
  shell [servicio]   - Abrir shell en contenedor
  restart            - Reiniciar contenedores
  rebuild            - Reconstruir y levantar
  clean              - Eliminar todo (CUIDADO!)
  health             - Verificar salud de servicios
  backup             - Crear backup de BD
  restore [archivo]  - Restaurar BD desde backup
  help               - Mostrar esta ayuda

${GREEN}Ejemplos:${NC}
  ./docker-control.sh up
  ./docker-control.sh logs sportmap-db
  ./docker-control.sh psql
  ./docker-control.sh backup
  ./docker-control.sh restore backup_20260609_101530.sql

${YELLOW}Servicios disponibles:${NC}
  - sportmap-db      (PostgreSQL)
  - pgadmin          (Interfaz de BD)
  - sportmap-redis   (Cache)

EOF
}

# Main
COMMAND="${1:-help}"

case "$COMMAND" in
    up)
        cmd_up
        ;;
    down)
        cmd_down
        ;;
    status)
        cmd_status
        ;;
    logs)
        cmd_logs_service "$@"
        ;;
    psql)
        cmd_psql
        ;;
    shell)
        cmd_shell "$@"
        ;;
    restart)
        cmd_restart
        ;;
    rebuild)
        cmd_rebuild
        ;;
    clean)
        cmd_clean
        ;;
    health)
        cmd_health
        ;;
    backup)
        cmd_backup
        ;;
    restore)
        cmd_restore "$@"
        ;;
    help)
        cmd_help
        ;;
    *)
        print_error "Comando desconocido: $COMMAND"
        cmd_help
        exit 1
        ;;
esac
