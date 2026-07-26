import time
import uuid
from pathlib import Path
from typing import Optional

from fastapi import APIRouter, File, Form, HTTPException, UploadFile

from app.database import get_pool
from app.models import OrderResponse
from app.supabase_storage import BUCKET_COMPROBANTES, is_configured, upload_file

router = APIRouter(prefix="/api/orders", tags=["orders"])

# Fallback a disco local si Supabase todavía no está configurado (útil en
# desarrollo). En Railway el disco es efímero, así que en producción SIEMPRE
# debería estar configurado Supabase para que los comprobantes no se pierdan
# en cada redeploy.
UPLOAD_DIR = Path(__file__).resolve().parent.parent / "uploads" / "comprobantes"
UPLOAD_DIR.mkdir(parents=True, exist_ok=True)

ALLOWED_TYPES = {"store", "reservation", "premium"}
ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".webp"}


def _to_resp(r) -> OrderResponse:
    return OrderResponse(
        id=r["id"],
        user_id=r["user_id"],
        order_type=r["order_type"],
        reference_id=r["reference_id"],
        amount=float(r["amount"]),
        items_json=r["items_json"],
        status=r["status"],
        proof_image_path=r["proof_image_path"],
        admin_note=r["admin_note"],
        motivo_rechazo=r["motivo_rechazo"],
        created_at=r["created_at"],
        reviewed_at=r["reviewed_at"],
    )


@router.post("/", response_model=OrderResponse)
async def create_order(
    user_id: int = Form(...),
    order_type: str = Form(...),
    amount: float = Form(...),
    reference_id: Optional[int] = Form(None),
    items_json: Optional[str] = Form(None),
    comprobante: UploadFile = File(...),
):
    if order_type not in ALLOWED_TYPES:
        raise HTTPException(status_code=400, detail="order_type inválido")

    content = await comprobante.read()

    if is_configured():
        proof_url = upload_file(
            BUCKET_COMPROBANTES, content, comprobante.filename or "comprobante.jpg",
            comprobante.content_type or "image/jpeg",
        )
    else:
        ext = Path(comprobante.filename or "comprobante.jpg").suffix.lower()
        if ext not in ALLOWED_EXTENSIONS:
            ext = ".jpg"
        filename = f"{uuid.uuid4().hex}{ext}"
        (UPLOAD_DIR / filename).write_bytes(content)
        proof_url = f"/uploads/comprobantes/{filename}"

    now = int(time.time() * 1000)
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow(
            """INSERT INTO orders
               (user_id, order_type, reference_id, amount, items_json, proof_image_path, created_at)
               VALUES ($1,$2,$3,$4,$5,$6,$7)
               RETURNING *""",
            user_id, order_type, reference_id, amount, items_json,
            proof_url, now,
        )
    return _to_resp(row)


@router.post("/{order_id}/reupload", response_model=OrderResponse)
async def reupload_proof(order_id: int, comprobante: UploadFile = File(...)):
    """Vuelve a subir el comprobante de un pedido rechazado: reemplaza la
    imagen y regresa el mismo pedido a 'pendiente' para que el admin lo
    revise de nuevo, en vez de crear un pedido duplicado."""
    content = await comprobante.read()

    if is_configured():
        proof_url = upload_file(
            BUCKET_COMPROBANTES, content, comprobante.filename or "comprobante.jpg",
            comprobante.content_type or "image/jpeg",
        )
    else:
        ext = Path(comprobante.filename or "comprobante.jpg").suffix.lower()
        if ext not in ALLOWED_EXTENSIONS:
            ext = ".jpg"
        filename = f"{uuid.uuid4().hex}{ext}"
        (UPLOAD_DIR / filename).write_bytes(content)
        proof_url = f"/uploads/comprobantes/{filename}"

    now = int(time.time() * 1000)
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow(
            """UPDATE orders
               SET proof_image_path = $2, status = 'pendiente', motivo_rechazo = NULL,
                   reviewed_at = NULL, created_at = $3
               WHERE id = $1
               RETURNING *""",
            order_id, proof_url, now,
        )
        if not row:
            raise HTTPException(status_code=404, detail="Pedido no encontrado")
    return _to_resp(row)


@router.get("/user/{user_id}", response_model=list[OrderResponse])
async def list_user_orders(user_id: int):
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch(
            "SELECT * FROM orders WHERE user_id = $1 ORDER BY id DESC", user_id
        )
    return [_to_resp(r) for r in rows]
