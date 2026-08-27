package com.tunalex.sportmap.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Emoji que representa a cada tipo de deporte. Es la fuente única: la usan
 * tanto los marcadores del mapa como cualquier otro lugar que quiera mostrar
 * el ícono del deporte. Las claves coinciden con `PlaceEntity.sportType`
 * (valores del backend: futbol, voley, basquetbol, tenis, natacion,
 * ciclismo, correr, bienestar) y también con las etiquetas en inglés por si
 * acaso.
 */
fun emojiForSport(sportType: String?): String = when (sportType?.trim()?.lowercase()) {
    "futbol", "fútbol", "soccer", "football" -> "⚽"
    "voley", "vóley", "volley", "volleyball" -> "🏐"
    "basquetbol", "básquetbol", "basketball", "basket" -> "🏀"
    "tenis", "tennis" -> "🎾"
    "natacion", "natación", "swim", "swimming" -> "🏊"
    "ciclismo", "bike", "cycling", "bici" -> "🚴"
    "correr", "running", "run", "trote" -> "🏃"
    "bienestar", "wellness", "yoga" -> "🧘"
    else -> "📍"
}

// Los BitmapDescriptor son inmutables y solo hay ~8 emojis distintos, así que
// se cachean por (emoji + tamaño) para no redibujar el bitmap en cada frame.
private val iconCache = HashMap<String, BitmapDescriptor>()

/**
 * Pin rojo (el color del marcador de siempre) con el emoji del deporte
 * encima, listo para `Marker(icon = ...)`. Debe llamarse desde dentro del
 * contenido de un `GoogleMap { }`, cuando el SDK de Maps ya está inicializado.
 */
fun sportMarkerIcon(sportType: String?, sizePx: Int): BitmapDescriptor {
    val emoji = emojiForSport(sportType)
    val key = "$emoji@$sizePx"
    iconCache[key]?.let { return it }
    val descriptor = BitmapDescriptorFactory.fromBitmap(buildSportMarkerBitmap(emoji, sizePx))
    iconCache[key] = descriptor
    return descriptor
}

private fun buildSportMarkerBitmap(emoji: String, size: Int): Bitmap {
    val width = size
    val height = (size * 1.3f).toInt()
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    val cx = width / 2f
    val cy = width / 2f
    val radius = width / 2f - width * 0.06f

    val fill = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E53935")
        style = Paint.Style.FILL
    }
    val border = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = width * 0.06f
    }

    // Cola del pin (triángulo que apunta a la coordenada exacta).
    val tail = Path().apply {
        moveTo(cx - radius * 0.5f, cy + radius * 0.45f)
        lineTo(cx + radius * 0.5f, cy + radius * 0.45f)
        lineTo(cx, height - width * 0.04f)
        close()
    }
    canvas.drawPath(tail, fill)
    canvas.drawCircle(cx, cy, radius, fill)
    canvas.drawCircle(cx, cy, radius, border)

    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textSize = width * 0.5f
    }
    val fm = textPaint.fontMetrics
    val baseline = cy - (fm.ascent + fm.descent) / 2f
    canvas.drawText(emoji, cx, baseline, textPaint)

    return bitmap
}
