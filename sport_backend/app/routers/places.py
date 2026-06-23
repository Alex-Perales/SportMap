from fastapi import APIRouter, Query
from app.database import get_pool
from app.models import PlaceResponse
from typing import Optional

router = APIRouter(prefix="/api/places", tags=["places"])


def _row_to_place(row) -> PlaceResponse:
    return PlaceResponse(
        id=row["id"],
        name=row["name"],
        sport_type=row["sport_type"],
        category=row["category"],
        lat=row["lat"],
        lng=row["lng"],
        is_private=row["is_private"],
        description=row["description"],
        services=row["services"],
        photo_urls=row["photo_urls"],
        rating=row["rating"] or 0.0,
        price_per_hour=row["price_per_hour"] or 0.0,
        air_quality_index=row["air_quality_index"] or 50,
    )


@router.get("/", response_model=list[PlaceResponse])
async def list_places(sport_type: Optional[str] = Query(None)):
    pool = await get_pool()
    async with pool.acquire() as conn:
        if sport_type:
            rows = await conn.fetch(
                "SELECT * FROM places WHERE sport_type = $1 ORDER BY id", sport_type
            )
        else:
            rows = await conn.fetch("SELECT * FROM places ORDER BY id")
        return [_row_to_place(r) for r in rows]


@router.get("/{place_id}", response_model=PlaceResponse)
async def get_place(place_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow("SELECT * FROM places WHERE id = $1", place_id)
        if not row:
            from fastapi import HTTPException
            raise HTTPException(status_code=404, detail="Lugar no encontrado")
        return _row_to_place(row)
