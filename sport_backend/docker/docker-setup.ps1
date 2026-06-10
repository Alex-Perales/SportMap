# ===================================
# DOCKER SETUP - Windows (PowerShell)
# ===================================

# Copiar archivo .env
if (Test-Path ".\.env") {
    Write-Host "✓ .env ya existe" -ForegroundColor Green
} else {
    Copy-Item ".\.env.example" ".\.env"
    Write-Host "✓ .env creado desde .env.example" -ForegroundColor Green
}

# Levantar contenedores
Write-Host "Levantando contenedores Docker..." -ForegroundColor Cyan
docker-compose up -d

# Esperar a que la BD esté lista
Write-Host "Esperando a que PostgreSQL esté listo..." -ForegroundColor Yellow
Start-Sleep -Seconds 5

# Verificar conexión
Write-Host "Verificando conexión..." -ForegroundColor Cyan
$connected = $false
for ($i = 0; $i -lt 30; $i++) {
    try {
        docker-compose exec -T sportmap-db pg_isready -U alex | Out-Null
        if ($LASTEXITCODE -eq 0) {
            $connected = $true
            break
        }
    } catch {
        # Intentar de nuevo
    }
    Start-Sleep -Seconds 1
}

if ($connected) {
    Write-Host "✓ PostgreSQL está listo" -ForegroundColor Green
    
    # Mostrar información de conexión
    Write-Host ""
    Write-Host "===================================" -ForegroundColor Cyan
    Write-Host "   INFORMACIÓN DE CONEXIÓN" -ForegroundColor Cyan
    Write-Host "===================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "PostgreSQL:" -ForegroundColor Green
    Write-Host "  Host: localhost" 
    Write-Host "  Port: 5432"
    Write-Host "  User: alex"
    Write-Host "  Password: 121416"
    Write-Host "  Database: sportmap_db"
    Write-Host ""
    Write-Host "pgAdmin (Interfaz de BD):" -ForegroundColor Green
    Write-Host "  URL: http://localhost:5050"
    Write-Host "  Email: alex@gmail.com"
    Write-Host "  Password: 121416"
    Write-Host ""
    Write-Host "Redis (Cache):" -ForegroundColor Green
    Write-Host "  Host: localhost"
    Write-Host "  Port: 6379"
    Write-Host ""
    Write-Host "===================================" -ForegroundColor Cyan
} else {
    Write-Host "✗ PostgreSQL no respondió a tiempo" -ForegroundColor Red
    Write-Host "Intenta: docker-compose logs sportmap-db" -ForegroundColor Yellow
}
