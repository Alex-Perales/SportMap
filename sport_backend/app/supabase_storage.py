"""Subida de archivos a Supabase Storage. Solo se guarda la URL pública en
Postgres — el binario nunca toca la base de datos.

Requiere las variables de entorno SUPABASE_URL y SUPABASE_SERVICE_KEY (la
"service_role key" del proyecto, con permisos de administrador sobre
Storage — no la anon key). Sin ellas, `upload_file` lanza una excepción
clara en vez de fallar de forma confusa más abajo.
"""
import logging
import os
import uuid
from pathlib import Path

logger = logging.getLogger("sportmap.supabase")

SUPABASE_URL = os.getenv("SUPABASE_URL")
SUPABASE_SERVICE_KEY = os.getenv("SUPABASE_SERVICE_KEY")

BUCKET_COMPROBANTES = "comprobantes"
BUCKET_PERFILES = "perfiles"
BUCKET_PRODUCTOS = "productos"
BUCKET_LUGARES = "lugares"
ALL_BUCKETS = [BUCKET_COMPROBANTES, BUCKET_PERFILES, BUCKET_PRODUCTOS, BUCKET_LUGARES]

_client = None


def is_configured() -> bool:
    return bool(SUPABASE_URL and SUPABASE_SERVICE_KEY)


def _get_client():
    global _client
    if _client is None:
        if not is_configured():
            raise RuntimeError(
                "Supabase no está configurado: define SUPABASE_URL y SUPABASE_SERVICE_KEY."
            )
        from supabase import create_client
        _client = create_client(SUPABASE_URL, SUPABASE_SERVICE_KEY)
    return _client


async def ensure_buckets():
    """Crea los buckets si no existen todavía. Se llama una vez al arrancar
    la API; si Supabase no está configurado, no hace nada (no rompe el
    arranque para quien todavía no lo haya configurado)."""
    if not is_configured():
        logger.warning("SUPABASE_URL/SUPABASE_SERVICE_KEY no configurados: Storage deshabilitado.")
        return
    client = _get_client()
    existing = {b.name for b in client.storage.list_buckets()}
    for bucket in ALL_BUCKETS:
        if bucket not in existing:
            try:
                client.storage.create_bucket(bucket, options={"public": True})
            except Exception:
                logger.exception("No se pudo crear el bucket '%s'", bucket)


def upload_file(bucket: str, content: bytes, original_filename: str, content_type: str) -> str:
    """Sube `content` al bucket indicado con un nombre único y devuelve la
    URL pública. Lanza excepción si Supabase no está configurado o falla
    la subida — quien llama decide cómo manejarlo (igual que el resto de
    llamadas de red en este backend)."""
    client = _get_client()
    ext = Path(original_filename).suffix.lower() or ".jpg"
    path = f"{uuid.uuid4().hex}{ext}"
    client.storage.from_(bucket).upload(
        path, content, {"content-type": content_type or "application/octet-stream"}
    )
    return client.storage.from_(bucket).get_public_url(path)
