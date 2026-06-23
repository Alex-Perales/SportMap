from fastapi import APIRouter, HTTPException
from app.database import get_pool
from app.models import AuthResponse, UserUpdate
import time

router = APIRouter(prefix="/api/users", tags=["users"])


@router.get("/{user_id}", response_model=AuthResponse)
async def get_user(user_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow(
            "SELECT id, name, email, district, is_premium, profile_image_url, created_at FROM users WHERE id = $1",
            user_id
        )
        if not row:
            raise HTTPException(status_code=404, detail="Usuario no encontrado")
        return AuthResponse(
            id=row["id"],
            name=row["name"],
            email=row["email"],
            district=row["district"] or "Miraflores",
            is_premium=row["is_premium"],
            profile_image_url=row["profile_image_url"],
            created_at=row["created_at"],
        )


@router.patch("/{user_id}", response_model=AuthResponse)
async def update_user(user_id: int, body: UserUpdate):
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow("SELECT * FROM users WHERE id = $1", user_id)
        if not row:
            raise HTTPException(status_code=404, detail="Usuario no encontrado")

        now = int(time.time() * 1000)
        updated = await conn.fetchrow(
            """UPDATE users SET
                 name = COALESCE($2, name),
                 district = COALESCE($3, district),
                 profile_image_url = COALESCE($4, profile_image_url),
                 is_premium = COALESCE($5, is_premium),
                 updated_at = $6
               WHERE id = $1
               RETURNING id, name, email, district, is_premium, profile_image_url, created_at""",
            user_id, body.name, body.district, body.profile_image_url, body.is_premium, now
        )
        return AuthResponse(
            id=updated["id"],
            name=updated["name"],
            email=updated["email"],
            district=updated["district"] or "Miraflores",
            is_premium=updated["is_premium"],
            profile_image_url=updated["profile_image_url"],
            created_at=updated["created_at"],
        )


@router.delete("/{user_id}")
async def delete_user(user_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute("DELETE FROM users WHERE id = $1", user_id)
        return {"ok": True}
