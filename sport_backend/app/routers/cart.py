from fastapi import APIRouter, HTTPException
from app.database import get_pool
from app.models import CartItemCreate, CartItemResponse
import time

router = APIRouter(prefix="/api/cart", tags=["cart"])


def _to_resp(r) -> CartItemResponse:
    return CartItemResponse(
        id=r["id"],
        user_id=r["user_id"],
        product_id=r["product_id"],
        product_name=r["product_name"] or "",
        product_image_url=r["product_image_url"] or "",
        unit_price=float(r["unit_price"] or 0),
        quantity=r["quantity"] or 1,
        selected_size=r["selected_size"] or "",
    )


@router.get("/{user_id}", response_model=list[CartItemResponse])
async def get_cart(user_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch(
            "SELECT * FROM cart_items WHERE user_id = $1 ORDER BY id", user_id
        )
        return [_to_resp(r) for r in rows]


@router.post("/", response_model=CartItemResponse)
async def add_to_cart(body: CartItemCreate):
    pool = await get_pool()
    async with pool.acquire() as conn:
        now = int(time.time() * 1000)
        row = await conn.fetchrow(
            """INSERT INTO cart_items
               (user_id, product_id, product_name, product_image_url, unit_price, quantity, selected_size, added_at)
               VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
               RETURNING id, user_id, product_id, product_name, product_image_url, unit_price, quantity, selected_size""",
            body.user_id, body.product_id, body.product_name, body.product_image_url,
            body.unit_price, body.quantity, body.selected_size, now
        )
        return _to_resp(row)


@router.delete("/item/{item_id}")
async def remove_item(item_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow("SELECT id FROM cart_items WHERE id = $1", item_id)
        if not row:
            raise HTTPException(status_code=404, detail="Item no encontrado")
        await conn.execute("DELETE FROM cart_items WHERE id = $1", item_id)
        return {"ok": True}


@router.delete("/user/{user_id}")
async def clear_cart(user_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute("DELETE FROM cart_items WHERE user_id = $1", user_id)
        return {"ok": True}
