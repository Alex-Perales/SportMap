"""Notificaciones push vía Firebase Cloud Messaging (FCM).

Credenciales: por defecto lee el JSON completo de la cuenta de servicio desde
la variable de entorno FIREBASE_SERVICE_ACCOUNT_JSON (así se configura en
Railway, como secret). En desarrollo local, si esa variable no está, cae a
FIREBASE_SERVICE_ACCOUNT_PATH (ruta a un archivo .json montado en el
contenedor). Sin ninguna de las dos, el envío se omite con una advertencia,
igual que el correo — no debe romper el flujo de aprobar un pedido.
"""
import json
import logging
import os

logger = logging.getLogger("sportmap.push")

_initialized = False


def _ensure_initialized() -> bool:
    global _initialized
    if _initialized:
        return True

    service_account_json = os.getenv("FIREBASE_SERVICE_ACCOUNT_JSON")
    service_account_path = os.getenv("FIREBASE_SERVICE_ACCOUNT_PATH", "/backend/firebase-adminsdk.json")

    import firebase_admin
    from firebase_admin import credentials

    try:
        if service_account_json:
            cred = credentials.Certificate(json.loads(service_account_json))
        elif os.path.exists(service_account_path):
            cred = credentials.Certificate(service_account_path)
        else:
            logger.warning(
                "No hay credenciales de Firebase (FIREBASE_SERVICE_ACCOUNT_JSON o %s): push deshabilitado.",
                service_account_path,
            )
            return False
        firebase_admin.initialize_app(cred)
        _initialized = True
        return True
    except ValueError:
        # initialize_app ya se llamó antes (p. ej. recarga en desarrollo)
        _initialized = True
        return True
    except Exception:
        logger.exception("No se pudo inicializar Firebase Admin.")
        return False


def send_push(fcm_token: str | None, title: str, body: str) -> bool:
    """Envía una notificación push. Devuelve True si se envió, False si no
    (sin token, sin credenciales, o error de FCM). Nunca lanza excepción."""
    if not fcm_token:
        return False
    if not _ensure_initialized():
        return False

    from firebase_admin import messaging

    try:
        message = messaging.Message(
            notification=messaging.Notification(title=title, body=body),
            token=fcm_token,
        )
        messaging.send(message)
        return True
    except Exception:
        logger.exception("Error enviando push a token %s...", fcm_token[:12])
        return False
