from pydantic import BaseModel
from typing import Optional


# ── Auth ────────────────────────────────────────────────────────────────────

class RegisterRequest(BaseModel):
    name: str
    email: str
    password_hash: str  # SHA-256 ya calculado en Android

class LoginRequest(BaseModel):
    email: str
    password_hash: str

class AuthResponse(BaseModel):
    id: int
    name: str
    email: str
    district: str
    is_premium: bool
    profile_image_url: Optional[str] = None
    created_at: int


# ── User ────────────────────────────────────────────────────────────────────

class UserUpdate(BaseModel):
    name: Optional[str] = None
    district: Optional[str] = None
    profile_image_url: Optional[str] = None
    is_premium: Optional[bool] = None


# ── Place ───────────────────────────────────────────────────────────────────

class PlaceResponse(BaseModel):
    id: int
    name: str
    sport_type: str
    category: str
    lat: float
    lng: float
    is_private: bool
    description: Optional[str] = None
    services: Optional[str] = None
    photo_urls: Optional[str] = None
    rating: float
    price_per_hour: float
    air_quality_index: int


# ── Activity ─────────────────────────────────────────────────────────────────

class ActivityCreate(BaseModel):
    user_id: int
    type: str
    distance_km: float
    duration_minutes: int
    place_id: Optional[int] = None
    date: int  # milisegundos epoch

class ActivityResponse(ActivityCreate):
    id: int


# ── Reservation ──────────────────────────────────────────────────────────────

class ReservationCreate(BaseModel):
    user_id: int
    place_id: int
    place_name: str
    date: int
    time: str
    people_count: int
    status: str = "confirmed"
    created_at: int

class ReservationResponse(ReservationCreate):
    id: int


# ── Product ──────────────────────────────────────────────────────────────────

class ProductResponse(BaseModel):
    id: int
    name: str
    description: Optional[str] = None
    price: float
    image_url: Optional[str] = None
    category: str
    sizes: Optional[str] = None
    stock: int
    is_on_sale: bool
    discount_percent: int


# ── Cart ─────────────────────────────────────────────────────────────────────

class CartItemCreate(BaseModel):
    user_id: int
    product_id: int
    product_name: str
    product_image_url: str
    unit_price: float
    quantity: int
    selected_size: str

class CartItemResponse(CartItemCreate):
    id: int


# ── Medal ────────────────────────────────────────────────────────────────────

class MedalCreate(BaseModel):
    user_id: int
    name: str
    description: str
    icon_key: str
    earned: bool
    earned_date: Optional[int] = None
    tier: str

class MedalResponse(MedalCreate):
    id: int
