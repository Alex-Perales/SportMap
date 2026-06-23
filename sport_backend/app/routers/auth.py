from fastapi import APIRouter, HTTPException
from app.database import get_pool
from app.models import RegisterRequest, LoginRequest, AuthResponse
import time

router = APIRouter(prefix="/api/auth", tags=["auth"])


@router.post("/register", response_model=AuthResponse)
async def register(req: RegisterRequest):
    pool = await get_pool()
    async with pool.acquire() as conn:
        existing = await conn.fetchrow(
            "SELECT id FROM users WHERE email = $1", req.email.lower().strip()
        )
        if existing:
            raise HTTPException(status_code=409, detail="Email ya registrado")

        now = int(time.time() * 1000)
        row = await conn.fetchrow(
            """INSERT INTO users (name, email, password_hash, district, is_premium, created_at, updated_at)
               VALUES ($1, $2, $3, 'Miraflores', FALSE, $4, $4)
               RETURNING id, name, email, district, is_premium, profile_image_url, created_at""",
            req.name.strip(), req.email.lower().strip(), req.password_hash, now
        )

        # Insert default medals
        user_id = row["id"]
        medals = [
            (user_id, "Corredor madrugador", "Completa 5 carreras antes de las 7am", "morning", True, now, "gold"),
            (user_id, "Dueño de la cancha", "Reserva 10 canchas en un mes", "field", True, now, "silver"),
            (user_id, "Explorador urbano", "Visita 15 lugares deportivos diferentes", "explorer", False, None, "gold"),
            (user_id, "Ciclista de acero", "Acumula 100 km en bicicleta", "cycling", True, now, "bronze"),
            (user_id, "Sirena", "Nada 20 km en piscina", "swimming", False, None, "silver"),
            (user_id, "Maratonista", "Completa una distancia de 42 km en running", "marathon", False, None, "gold"),
            (user_id, "Yogui", "Visita 5 zonas de bienestar", "wellness", True, now, "bronze"),
            (user_id, "Capitán", "Organiza 5 partidos con tu grupo", "captain", False, None, "silver"),
        ]
        await conn.executemany(
            """INSERT INTO medals (user_id, name, description, icon_key, earned, earned_date, tier, created_at)
               VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
               ON CONFLICT DO NOTHING""",
            [(m[0], m[1], m[2], m[3], m[4], m[5], m[6], now) for m in medals]
        )

        return AuthResponse(
            id=row["id"],
            name=row["name"],
            email=row["email"],
            district=row["district"] or "Miraflores",
            is_premium=row["is_premium"],
            profile_image_url=row["profile_image_url"],
            created_at=row["created_at"],
        )


@router.post("/login", response_model=AuthResponse)
async def login(req: LoginRequest):
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow(
            """SELECT id, name, email, district, is_premium, profile_image_url, created_at, password_hash
               FROM users WHERE email = $1""",
            req.email.lower().strip()
        )
        if not row:
            raise HTTPException(status_code=401, detail="Usuario no encontrado")
        if row["password_hash"] != req.password_hash:
            raise HTTPException(status_code=401, detail="Contraseña incorrecta")

        return AuthResponse(
            id=row["id"],
            name=row["name"],
            email=row["email"],
            district=row["district"] or "Miraflores",
            is_premium=row["is_premium"],
            profile_image_url=row["profile_image_url"],
            created_at=row["created_at"],
        )
