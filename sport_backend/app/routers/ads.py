from fastapi import APIRouter

from app.database import get_pool
from app.models import AdResponse

router = APIRouter(prefix="/api/ads", tags=["ads"])


def _to_resp(r) -> AdResponse:
    return AdResponse(
        id=r["id"],
        image_url=r["image_url"],
        badge_text=r["badge_text"],
        title=r["title"],
        subtitle=r["subtitle"],
        price=float(r["price"]) if r["price"] is not None else None,
        link_type=r["link_type"],
        link_target=r["link_target"],
        is_active=r["is_active"],
        sort_order=r["sort_order"],
    )


@router.get("/", response_model=list[AdResponse])
async def list_active_ads():
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch(
            "SELECT * FROM ads WHERE is_active = TRUE ORDER BY sort_order ASC, id ASC"
        )
    return [_to_resp(r) for r in rows]
