FROM python:3.12-slim

WORKDIR /backend

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY app/ ./app/
COPY pw_sportmap_src/dist/ ./pw_sportmap_src/dist/

EXPOSE 8000

# Respeta $PORT si el host (Render, Koyeb, etc.) lo inyecta; si no, usa 8000
# (Docker Compose local no lo define y sigue funcionando igual).
CMD ["sh", "-c", "uvicorn app.main:app --host 0.0.0.0 --port ${PORT:-8000}"]
