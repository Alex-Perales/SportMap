from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager

from app.database import get_pool, close_pool
from app.migrations import run_migrations
from app.seed import seed_places_and_products
from app.routers import auth, users, places, activities, reservations, products, cart, medals


@asynccontextmanager
async def lifespan(app: FastAPI):
    pool = await get_pool()
    async with pool.acquire() as conn:
        await run_migrations(conn)
        await seed_places_and_products(conn)
    yield
    await close_pool()


app = FastAPI(
    title="SportMap API",
    version="1.0.0",
    lifespan=lifespan,
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


@app.get("/health")
async def health():
    return {"status": "ok"}
