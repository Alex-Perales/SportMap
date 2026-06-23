from fastapi import APIRouter
from app.database import get_pool
from app.models import MedalCreate, MedalResponse

router = APIRouter(prefix="/api/medals", tags=["medals"])


def _to_resp(r) -> MedalResponse:
    return MedalResponse(
        id=r["id"],
        user_id=r["user_id"],
        name=r["name"],
        description=r["description"],
        icon_key=r["icon_key"],
        earned=r["earned"],
        earned_date=r["earned_date"],
        tier=r["tier"],
    )


@router.get("/user/{user_id}", response_model=list[MedalResponse])
async def get_user_medals(user_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch(
            "SELECT * FROM medals WHERE user_id = $1 ORDER BY id", user_id
        )
        return [_to_resp(r) for r in rows]


@router.post("/", response_model=MedalResponse)
async def create_medal(body: MedalCreate):
    pool = await get_pool()
    async with pool.acquire() as conn:
        import time
        now = int(time.time() * 1000)
        row = await conn.fetchrow(
            """INSERT INTO medals (user_id, name, description, icon_key, earned, earned_date, tier, created_at)
               VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
               RETURNING id, user_id, name, description, icon_key, earned, earned_date, tier""",
            body.user_id, body.name, body.description, body.icon_key,
            body.earned, body.earned_date, body.tier, now
        )
        return _to_resp(row)
