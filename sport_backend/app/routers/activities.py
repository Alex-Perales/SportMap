from fastapi import APIRouter
from app.database import get_pool
from app.models import ActivityCreate, ActivityResponse

router = APIRouter(prefix="/api/activities", tags=["activities"])


@router.post("/", response_model=ActivityResponse)
async def create_activity(body: ActivityCreate):
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow(
            """INSERT INTO activities
               (user_id, type, distance_km, duration_minutes, calories_burned, place_id, date, created_at)
               VALUES ($1, $2, $3, $4, 0, $5, $6, $6)
               RETURNING id, user_id, type, distance_km, duration_minutes, place_id, date""",
            body.user_id, body.type, body.distance_km, body.duration_minutes,
            body.place_id, body.date
        )
        return ActivityResponse(
            id=row["id"],
            user_id=row["user_id"],
            type=row["type"],
            distance_km=float(row["distance_km"]),
            duration_minutes=row["duration_minutes"],
            place_id=row["place_id"],
            date=row["date"],
        )


@router.get("/user/{user_id}", response_model=list[ActivityResponse])
async def get_user_activities(user_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch(
            "SELECT * FROM activities WHERE user_id = $1 ORDER BY date DESC",
            user_id
        )
        return [
            ActivityResponse(
                id=r["id"],
                user_id=r["user_id"],
                type=r["type"],
                distance_km=float(r["distance_km"]),
                duration_minutes=r["duration_minutes"],
                place_id=r["place_id"],
                date=r["date"],
            )
            for r in rows
        ]
