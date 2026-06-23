from fastapi import APIRouter, HTTPException
from app.database import get_pool
from app.models import ReservationCreate, ReservationResponse
import time

router = APIRouter(prefix="/api/reservations", tags=["reservations"])


def _to_resp(r) -> ReservationResponse:
    return ReservationResponse(
        id=r["id"],
        user_id=r["user_id"],
        place_id=r["place_id"],
        place_name=r["place_name"],
        date=r["reservation_date"],
        time=r["start_time"] or "",
        people_count=r["people_count"] or 1,
        status=r["status"],
        created_at=r["created_at"],
    )


@router.post("/", response_model=ReservationResponse)
async def create_reservation(body: ReservationCreate):
    pool = await get_pool()
    async with pool.acquire() as conn:
        now = int(time.time() * 1000)
        row = await conn.fetchrow(
            """INSERT INTO reservations
               (user_id, place_id, place_name, reservation_date, start_time, people_count, status, created_at, updated_at)
               VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $8)
               RETURNING id, user_id, place_id, place_name, reservation_date, start_time, people_count, status, created_at""",
            body.user_id, body.place_id, body.place_name, body.date,
            body.time, body.people_count, body.status, body.created_at
        )
        return _to_resp(row)


@router.get("/user/{user_id}", response_model=list[ReservationResponse])
async def get_user_reservations(user_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch(
            "SELECT * FROM reservations WHERE user_id = $1 ORDER BY reservation_date DESC",
            user_id
        )
        return [_to_resp(r) for r in rows]


@router.delete("/{reservation_id}")
async def cancel_reservation(reservation_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow(
            "SELECT id FROM reservations WHERE id = $1", reservation_id
        )
        if not row:
            raise HTTPException(status_code=404, detail="Reserva no encontrada")
        await conn.execute("DELETE FROM reservations WHERE id = $1", reservation_id)
        return {"ok": True}
