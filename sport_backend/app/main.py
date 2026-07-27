import os
from pathlib import Path

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from fastapi.staticfiles import StaticFiles
from fastapi.responses import FileResponse
from starlette.middleware.sessions import SessionMiddleware
from contextlib import asynccontextmanager

from app.database import get_pool, close_pool
from app.migrations import run_migrations
from app.seed import seed_places_and_products, seed_admin_user, seed_ads
from app.routers import auth, users, places, activities, reservations, products, cart, medals, admin, orders, ads
from app.supabase_storage import ensure_buckets


@asynccontextmanager
async def lifespan(app: FastAPI):
    pool = await get_pool()
    async with pool.acquire() as conn:
        await run_migrations(conn)
        await seed_places_and_products(conn)
        await seed_admin_user(conn)
        await seed_ads(conn)
    try:
        await ensure_buckets()
    except Exception:
        pass
    yield
    await close_pool()


app = FastAPI(
    title="SportMap API",
    version="1.0.0",
    lifespan=lifespan,
)

app.add_middleware(
    SessionMiddleware,
    secret_key=os.getenv("SECRET_KEY", "sportmap-dev-secret-change-me"),
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(auth.router)
app.include_router(users.router)
app.include_router(places.router)
app.include_router(activities.router)
app.include_router(reservations.router)
app.include_router(products.router)
app.include_router(cart.router)
app.include_router(medals.router)
app.include_router(admin.router)
app.include_router(orders.router)
app.include_router(ads.router)

_uploads_dir = Path(__file__).resolve().parent / "uploads"
_uploads_dir.mkdir(exist_ok=True)
app.mount("/uploads", StaticFiles(directory=str(_uploads_dir)), name="uploads")

# Landing page pública (build de sport_backend/pw_sportmap_src, generado con `npm run build`)
_web_dist_dir = Path(__file__).resolve().parent.parent / "pw_sportmap_src" / "dist"
app.mount("/assets", StaticFiles(directory=str(_web_dist_dir / "assets")), name="web-assets")


@app.get("/")
async def home():
    return FileResponse(str(_web_dist_dir / "index.html"))


@app.get("/health")
async def health():
    return {"status": "ok"}
