"""Envío de correos de confirmación vía Gmail SMTP (App Password).

No es un comprobante tributario válido ante SUNAT — es un correo de
confirmación interno para el proyecto. Requiere las variables de entorno
GMAIL_ADDRESS y GMAIL_APP_PASSWORD (contraseña de aplicación de Gmail,
no la contraseña normal de la cuenta). Si no están configuradas, el envío
se omite silenciosamente (se loguea una advertencia) para no romper el
flujo de aprobación de pedidos.
"""
import logging
import os
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText

logger = logging.getLogger("sportmap.email")

GMAIL_ADDRESS = os.getenv("GMAIL_ADDRESS")
GMAIL_APP_PASSWORD = os.getenv("GMAIL_APP_PASSWORD")


def is_configured() -> bool:
    return bool(GMAIL_ADDRESS and GMAIL_APP_PASSWORD)


def send_email(to_email: str, subject: str, html_body: str) -> bool:
    """Intenta enviar un correo. Devuelve True si se envió, False si no
    (por falta de configuración o error de SMTP). Nunca lanza excepción."""
    if not is_configured():
        logger.warning(
            "GMAIL_ADDRESS / GMAIL_APP_PASSWORD no configurados: correo a %s no enviado.",
            to_email,
        )
        return False

    msg = MIMEMultipart("alternative")
    msg["Subject"] = subject
    msg["From"] = GMAIL_ADDRESS
    msg["To"] = to_email
    msg.attach(MIMEText(html_body, "html"))

    try:
        with smtplib.SMTP_SSL("smtp.gmail.com", 465) as server:
            server.login(GMAIL_ADDRESS, GMAIL_APP_PASSWORD)
            server.sendmail(GMAIL_ADDRESS, to_email, msg.as_string())
        return True
    except Exception:
        logger.exception("Error enviando correo a %s", to_email)
        return False


def order_approved_email(order_id: int, order_type: str, amount: float) -> tuple[str, str]:
    label = {
        "store": "tu compra en la tienda",
        "reservation": "tu reserva",
        "premium": "tu suscripción SportMap Pro",
    }.get(order_type, "tu pedido")

    subject = f"SportMap · Pago confirmado (pedido #{order_id})"
    html = f"""
    <div style="font-family:sans-serif;max-width:480px;margin:auto">
        <h2 style="color:#2563eb">¡Pago confirmado!</h2>
        <p>Hemos verificado tu comprobante de pago para {label}.</p>
        <p><b>Pedido:</b> #{order_id}<br>
           <b>Monto:</b> S/. {amount:.2f}</p>
        <p style="color:#6b7280;font-size:13px">
            Este correo es una confirmación interna de SportMap, no un
            comprobante tributario electrónico.
        </p>
    </div>
    """
    return subject, html


def order_rejected_email(order_id: int, order_type: str, reason: str | None) -> tuple[str, str]:
    subject = f"SportMap · No pudimos confirmar tu pago (pedido #{order_id})"
    reason_html = f"<p><b>Motivo:</b> {reason}</p>" if reason else ""
    html = f"""
    <div style="font-family:sans-serif;max-width:480px;margin:auto">
        <h2 style="color:#dc2626">No pudimos confirmar tu pago</h2>
        <p>Revisamos el comprobante que enviaste para el pedido #{order_id} y no pudimos validarlo.</p>
        {reason_html}
        <p>Por favor intenta nuevamente desde la app o contáctanos si crees que es un error.</p>
    </div>
    """
    return subject, html
