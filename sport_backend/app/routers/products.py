from fastapi import APIRouter, Query
from app.database import get_pool
from app.models import ProductResponse
from typing import Optional

router = APIRouter(prefix="/api/products", tags=["products"])


def _to_resp(r) -> ProductResponse:
    return ProductResponse(
        id=r["id"],
        name=r["name"],
        description=r["description"],
        price=float(r["price"]),
        image_url=r["image_url"],
        category=r["category"],
        sizes=r["sizes"],
        stock=r["stock"],
        is_on_sale=r["is_on_sale"],
        discount_percent=r["discount_percent"] or 0,
    )


@router.get("/", response_model=list[ProductResponse])
async def list_products(category: Optional[str] = Query(None)):
    pool = await get_pool()
    async with pool.acquire() as conn:
        if category:
            rows = await conn.fetch(
                "SELECT * FROM products WHERE category = $1 ORDER BY id", category
            )
        else:
            rows = await conn.fetch("SELECT * FROM products ORDER BY id")
        return [_to_resp(r) for r in rows]


@router.get("/{product_id}", response_model=ProductResponse)
async def get_product(product_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow("SELECT * FROM products WHERE id = $1", product_id)
        if not row:
            from fastapi import HTTPException
            raise HTTPException(status_code=404, detail="Producto no encontrado")
        return _to_resp(row)
