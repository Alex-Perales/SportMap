import hashlib
import time
import uuid
from pathlib import Path

from typing import Optional

from fastapi import APIRouter, File, Form, Request, UploadFile
from fastapi.responses import RedirectResponse
from fastapi.templating import Jinja2Templates

from app.database import get_pool
from app.email_utils import order_approved_email, order_rejected_email, send_email
from app.push_notifications import send_push
from app.supabase_storage import BUCKET_LUGARES, BUCKET_PRODUCTOS, is_configured, upload_file

router = APIRouter(prefix="/admin", tags=["admin"])

templates = Jinja2Templates(directory=str(Path(__file__).resolve().parent.parent / "templates"))

# Si Supabase todavía no está configurado (SUPABASE_URL / SUPABASE_SERVICE_KEY),
# las fotos subidas desde el panel se guardan aquí en vez de perderse en
# silencio. En cuanto configures Supabase, este fallback deja de usarse solo.
_LOCAL_UPLOADS_DIR = Path(__file__).resolve().parent.parent / "uploads"


def _save_upload_locally(subfolder: str, content: bytes, filename: str) -> str:
    ext = Path(filename).suffix.lower() or ".jpg"
    dest_dir = _LOCAL_UPLOADS_DIR / subfolder
    dest_dir.mkdir(parents=True, exist_ok=True)
    new_name = f"{uuid.uuid4().hex}{ext}"
    (dest_dir / new_name).write_bytes(content)
    return f"/uploads/{subfolder}/{new_name}"


async def _handle_image_upload(image_file: Optional[UploadFile], bucket: str, subfolder: str) -> Optional[str]:
    """Sube la imagen a Supabase si está configurado; si no, la guarda
    localmente para que la subida nunca falle en silencio. Devuelve None si
    no se adjuntó ningún archivo (para no pisar la URL pegada a mano)."""
    if image_file is None or not image_file.filename:
        return None
    content = await image_file.read()
    if not content:
        return None
    if is_configured():
        return upload_file(bucket, content, image_file.filename, image_file.content_type or "image/jpeg")
    return _save_upload_locally(subfolder, content, image_file.filename)


def _hash(password: str) -> str:
    return hashlib.sha256(password.encode()).hexdigest()


def _is_logged_in(request: Request) -> bool:
    return bool(request.session.get("admin_id"))


# ── Auth ────────────────────────────────────────────────────────────────────

@router.get("/login")
async def login_form(request: Request):
    if _is_logged_in(request):
        return RedirectResponse("/admin/products", status_code=303)
    return templates.TemplateResponse(request, "login.html", {})


@router.post("/login")
async def login_submit(request: Request, email: str = Form(...), password: str = Form(...)):
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow(
            "SELECT id, name, password_hash, is_admin FROM users WHERE email = $1",
            email.lower().strip(),
        )
    if not row or not row["is_admin"] or row["password_hash"] != _hash(password):
        return templates.TemplateResponse(
            request, "login.html",
            {"error": "Correo o contraseña incorrectos, o la cuenta no es administradora.", "email": email},
            status_code=401,
        )
    request.session["admin_id"] = row["id"]
    request.session["admin_name"] = row["name"]
    return RedirectResponse("/admin/products", status_code=303)


@router.get("/logout")
async def logout(request: Request):
    request.session.clear()
    return RedirectResponse("/admin/login", status_code=303)


@router.get("")
async def admin_root(request: Request):
    return RedirectResponse("/admin/products", status_code=303)


# ── Products CRUD ─────────────────────────────────────────────────────────────

@router.get("/products")
async def products_list(request: Request):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch("SELECT * FROM products ORDER BY id DESC")
    return templates.TemplateResponse(
        request, "products_list.html",
        {"products": rows, "flash": request.query_params.get("flash")},
    )


@router.get("/products/new")
async def product_new_form(request: Request):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    return templates.TemplateResponse(request, "product_form.html", {"product": None})


@router.post("/products/new")
async def product_new_submit(
    request: Request,
    name: str = Form(...),
    description: str = Form(""),
    price: float = Form(...),
    image_url: str = Form(""),
    image_file: Optional[UploadFile] = File(None),
    category: str = Form(""),
    sizes: str = Form(""),
    stock: int = Form(0),
    is_on_sale: bool = Form(False),
    discount_percent: int = Form(0),
):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)

    uploaded_url = await _handle_image_upload(image_file, BUCKET_PRODUCTOS, "productos")
    final_image_url = uploaded_url or image_url.strip()

    now = int(time.time() * 1000)
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute(
            """INSERT INTO products
               (name, description, price, image_url, category, sizes, stock, is_on_sale, discount_percent, created_at, updated_at)
               VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$10)""",
            name.strip(), description.strip(), price, final_image_url, category, sizes.strip(),
            stock, is_on_sale, discount_percent, now,
        )
    return RedirectResponse("/admin/products?flash=Producto creado", status_code=303)


@router.get("/products/{product_id}/edit")
async def product_edit_form(request: Request, product_id: int):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow("SELECT * FROM products WHERE id = $1", product_id)
    if not row:
        return RedirectResponse("/admin/products?flash=Producto no encontrado", status_code=303)
    return templates.TemplateResponse(request, "product_form.html", {"product": row})


@router.post("/products/{product_id}/edit")
async def product_edit_submit(
    request: Request,
    product_id: int,
    name: str = Form(...),
    description: str = Form(""),
    price: float = Form(...),
    image_url: str = Form(""),
    image_file: Optional[UploadFile] = File(None),
    category: str = Form(""),
    sizes: str = Form(""),
    stock: int = Form(0),
    is_on_sale: bool = Form(False),
    discount_percent: int = Form(0),
):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)

    uploaded_url = await _handle_image_upload(image_file, BUCKET_PRODUCTOS, "productos")
    final_image_url = uploaded_url or image_url.strip()

    now = int(time.time() * 1000)
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute(
            """UPDATE products SET name=$1, description=$2, price=$3, image_url=$4, category=$5,
               sizes=$6, stock=$7, is_on_sale=$8, discount_percent=$9, updated_at=$10
               WHERE id=$11""",
            name.strip(), description.strip(), price, final_image_url, category, sizes.strip(),
            stock, is_on_sale, discount_percent, now, product_id,
        )
    return RedirectResponse("/admin/products?flash=Cambios guardados", status_code=303)


@router.post("/products/{product_id}/delete")
async def product_delete(request: Request, product_id: int):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute("DELETE FROM products WHERE id = $1", product_id)
    return RedirectResponse("/admin/products?flash=Producto eliminado", status_code=303)


# ── Orders (pedidos pagados por Yape/Plin, pendientes de revisión) ───────────

@router.get("/orders")
async def orders_list(request: Request):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch(
            """SELECT o.*, u.name AS user_name, u.email AS user_email
               FROM orders o JOIN users u ON u.id = o.user_id
               WHERE o.status IN ('pendiente', 'rechazado', 'cancelado_reembolsado')
               ORDER BY o.id DESC"""
        )
    return templates.TemplateResponse(
        request, "orders_list.html",
        {"orders": rows, "flash": request.query_params.get("flash")},
    )


@router.post("/orders/{order_id}/approve")
async def order_approve(request: Request, order_id: int):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    now = int(time.time() * 1000)
    pool = await get_pool()
    async with pool.acquire() as conn:
        order = await conn.fetchrow(
            """SELECT o.*, u.email AS user_email, u.fcm_token AS user_fcm_token FROM orders o
               JOIN users u ON u.id = o.user_id WHERE o.id = $1""",
            order_id,
        )
        if not order:
            return RedirectResponse("/admin/orders?flash=Pedido no encontrado", status_code=303)

        await conn.execute(
            "UPDATE orders SET status = 'aprobado', reviewed_at = $1 WHERE id = $2",
            now, order_id,
        )
        if order["order_type"] == "reservation" and order["reference_id"]:
            await conn.execute(
                "UPDATE reservations SET status = 'confirmed', updated_at = $1 WHERE id = $2",
                now, order["reference_id"],
            )
        elif order["order_type"] == "premium":
            await conn.execute(
                "UPDATE users SET is_premium = TRUE, updated_at = $1 WHERE id = $2",
                now, order["user_id"],
            )

    subject, html = order_approved_email(order_id, order["order_type"], float(order["amount"]))
    send_email(order["user_email"], subject, html)
    send_push(order["user_fcm_token"], "¡Pago confirmado! ✅", "Ya validamos tu comprobante. Revisa los detalles en la app.")
    return RedirectResponse("/admin/orders?flash=Pedido aprobado", status_code=303)


@router.post("/orders/{order_id}/reject")
async def order_reject(
    request: Request,
    order_id: int,
    motivo: str = Form(...),
    motivo_otro: str = Form(""),
):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)

    motivo_final = motivo_otro.strip() if motivo == "Otro" and motivo_otro.strip() else motivo

    now = int(time.time() * 1000)
    pool = await get_pool()
    async with pool.acquire() as conn:
        order = await conn.fetchrow(
            """SELECT o.*, u.email AS user_email, u.fcm_token AS user_fcm_token FROM orders o
               JOIN users u ON u.id = o.user_id WHERE o.id = $1""",
            order_id,
        )
        if not order:
            return RedirectResponse("/admin/orders?flash=Pedido no encontrado", status_code=303)

        await conn.execute(
            "UPDATE orders SET status = 'rechazado', motivo_rechazo = $1, reviewed_at = $2 WHERE id = $3",
            motivo_final, now, order_id,
        )
        if order["order_type"] == "reservation" and order["reference_id"]:
            await conn.execute(
                "UPDATE reservations SET status = 'rechazada', updated_at = $1 WHERE id = $2",
                now, order["reference_id"],
            )

    subject, html = order_rejected_email(order_id, order["order_type"], motivo_final)
    send_email(order["user_email"], subject, html)
    send_push(
        order["user_fcm_token"], "No pudimos confirmar tu pago",
        f"Motivo: {motivo_final}. Puedes volver a subir tu comprobante desde la app.",
    )
    return RedirectResponse("/admin/orders?flash=Pedido rechazado", status_code=303)


@router.post("/orders/{order_id}/marcar-reembolsado")
async def order_mark_refunded(request: Request, order_id: int):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    now = int(time.time() * 1000)
    pool = await get_pool()
    async with pool.acquire() as conn:
        result = await conn.execute(
            "UPDATE orders SET status = 'cancelado_reembolsado', reviewed_at = $1 WHERE id = $2 AND status = 'rechazado'",
            now, order_id,
        )
        if result == "UPDATE 0":
            return RedirectResponse("/admin/orders?flash=Pedido no encontrado o ya no está rechazado", status_code=303)
    return RedirectResponse("/admin/orders?flash=Pedido marcado como reembolsado", status_code=303)


# ── Ads ("Recomendados para ti") ─────────────────────────────────────────────

@router.get("/ads")
async def ads_list(request: Request):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch("SELECT * FROM ads ORDER BY sort_order ASC, id ASC")
    return templates.TemplateResponse(
        request, "ads_list.html",
        {"ads": rows, "flash": request.query_params.get("flash")},
    )


@router.get("/ads/new")
async def ad_new_form(request: Request):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    return templates.TemplateResponse(request, "ad_form.html", {"ad": None})


@router.post("/ads/new")
async def ad_new_submit(
    request: Request,
    image_url: str = Form(...),
    badge_text: str = Form(""),
    title: str = Form(...),
    subtitle: str = Form(""),
    price: str = Form(""),
    link_type: str = Form("none"),
    link_target: str = Form(""),
    sort_order: int = Form(0),
    is_active: bool = Form(False),
):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    now = int(time.time() * 1000)
    price_value = float(price) if price.strip() else None
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute(
            """INSERT INTO ads
               (image_url, badge_text, title, subtitle, price, link_type, link_target, sort_order, is_active, created_at, updated_at)
               VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$10)""",
            image_url.strip(), badge_text.strip() or None, title.strip(), subtitle.strip() or None,
            price_value, link_type, link_target.strip() or None, sort_order, is_active, now,
        )
    return RedirectResponse("/admin/ads?flash=Anuncio creado", status_code=303)


@router.get("/ads/{ad_id}/edit")
async def ad_edit_form(request: Request, ad_id: int):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow("SELECT * FROM ads WHERE id = $1", ad_id)
    if not row:
        return RedirectResponse("/admin/ads?flash=Anuncio no encontrado", status_code=303)
    return templates.TemplateResponse(request, "ad_form.html", {"ad": row})


@router.post("/ads/{ad_id}/edit")
async def ad_edit_submit(
    request: Request,
    ad_id: int,
    image_url: str = Form(...),
    badge_text: str = Form(""),
    title: str = Form(...),
    subtitle: str = Form(""),
    price: str = Form(""),
    link_type: str = Form("none"),
    link_target: str = Form(""),
    sort_order: int = Form(0),
    is_active: bool = Form(False),
):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    now = int(time.time() * 1000)
    price_value = float(price) if price.strip() else None
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute(
            """UPDATE ads SET image_url=$1, badge_text=$2, title=$3, subtitle=$4, price=$5,
               link_type=$6, link_target=$7, sort_order=$8, is_active=$9, updated_at=$10
               WHERE id=$11""",
            image_url.strip(), badge_text.strip() or None, title.strip(), subtitle.strip() or None,
            price_value, link_type, link_target.strip() or None, sort_order, is_active, now, ad_id,
        )
    return RedirectResponse("/admin/ads?flash=Cambios guardados", status_code=303)


@router.post("/ads/{ad_id}/delete")
async def ad_delete(request: Request, ad_id: int):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute("DELETE FROM ads WHERE id = $1", ad_id)
    return RedirectResponse("/admin/ads?flash=Anuncio eliminado", status_code=303)


# ── Places (canchas / lugares del mapa) ───────────────────────────────────────

@router.get("/places")
async def places_list(request: Request):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        rows = await conn.fetch("SELECT * FROM places ORDER BY id DESC")
    return templates.TemplateResponse(
        request, "places_list.html",
        {"places": rows, "flash": request.query_params.get("flash")},
    )


@router.get("/places/new")
async def place_new_form(request: Request):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    return templates.TemplateResponse(request, "place_form.html", {"place": None})


@router.post("/places/new")
async def place_new_submit(
    request: Request,
    name: str = Form(...),
    sport_type: str = Form(...),
    category: str = Form(...),
    address: str = Form(""),
    lat: float = Form(...),
    lng: float = Form(...),
    description: str = Form(""),
    services: str = Form(""),
    photo_url: str = Form(""),
    photo_file: Optional[UploadFile] = File(None),
    is_private: bool = Form(False),
    price_per_hour: float = Form(0.0),
):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)

    uploaded_url = await _handle_image_upload(photo_file, BUCKET_LUGARES, "lugares")
    final_photo_url = uploaded_url or photo_url.strip()

    now = int(time.time() * 1000)
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute(
            """INSERT INTO places
               (name, sport_type, category, lat, lng, address, is_private, description,
                services, photo_urls, price_per_hour, created_at, updated_at)
               VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$12)""",
            name.strip(), sport_type, category, lat, lng, address.strip() or None, is_private,
            description.strip(), services.strip(), final_photo_url, price_per_hour, now,
        )
    return RedirectResponse("/admin/places?flash=Lugar creado", status_code=303)


@router.get("/places/{place_id}/edit")
async def place_edit_form(request: Request, place_id: int):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        row = await conn.fetchrow("SELECT * FROM places WHERE id = $1", place_id)
    if not row:
        return RedirectResponse("/admin/places?flash=Lugar no encontrado", status_code=303)
    return templates.TemplateResponse(request, "place_form.html", {"place": row})


@router.post("/places/{place_id}/edit")
async def place_edit_submit(
    request: Request,
    place_id: int,
    name: str = Form(...),
    sport_type: str = Form(...),
    category: str = Form(...),
    address: str = Form(""),
    lat: float = Form(...),
    lng: float = Form(...),
    description: str = Form(""),
    services: str = Form(""),
    photo_url: str = Form(""),
    photo_file: Optional[UploadFile] = File(None),
    is_private: bool = Form(False),
    price_per_hour: float = Form(0.0),
):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)

    uploaded_url = await _handle_image_upload(photo_file, BUCKET_LUGARES, "lugares")
    final_photo_url = uploaded_url or photo_url.strip()

    now = int(time.time() * 1000)
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute(
            """UPDATE places SET name=$1, sport_type=$2, category=$3, lat=$4, lng=$5, address=$6,
               is_private=$7, description=$8, services=$9, photo_urls=$10, price_per_hour=$11, updated_at=$12
               WHERE id=$13""",
            name.strip(), sport_type, category, lat, lng, address.strip() or None, is_private,
            description.strip(), services.strip(), final_photo_url, price_per_hour, now, place_id,
        )
    return RedirectResponse("/admin/places?flash=Cambios guardados", status_code=303)


@router.post("/places/{place_id}/delete")
async def place_delete(request: Request, place_id: int):
    if not _is_logged_in(request):
        return RedirectResponse("/admin/login", status_code=303)
    pool = await get_pool()
    async with pool.acquire() as conn:
        await conn.execute("DELETE FROM places WHERE id = $1", place_id)
    return RedirectResponse("/admin/places?flash=Lugar eliminado", status_code=303)
