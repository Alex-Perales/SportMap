package com.tunalex.sportmap.data.remote

/**
 * Normaliza las URLs de imágenes que llegan del backend para que la app
 * siempre pueda cargarlas, sin importar desde qué host se subió la foto en el
 * panel de administración:
 *
 *  - Ruta relativa ("/uploads/..."): se le antepone el host del backend que
 *    usa la app (10.0.2.2:8000 en el emulador, el dominio en release).
 *  - URL absoluta apuntando a localhost / 127.0.0.1 / 10.0.2.2: es una foto
 *    guardada en el disco del backend local pero con el host "quemado" por el
 *    navegador del admin. Se reescribe el host al del backend de la app.
 *  - Cualquier otra URL (Unsplash, Supabase, Railway, etc.): se deja igual.
 */
fun resolveBackendImageUrl(raw: String?): String {
    val url = raw?.trim().orEmpty()
    if (url.isEmpty()) return ""

    val origin = RetrofitClient.BASE_URL.trimEnd('/') // p.ej. http://10.0.2.2:8000

    if (url.startsWith("/")) return origin + url

    val lower = url.lowercase()
    if (lower.startsWith("http://") || lower.startsWith("https://")) {
        val afterHost = url.substringAfter("://").substringAfter('/', "")
        val host = url.substringAfter("://").substringBefore('/').lowercase()
        val isLoopback = host.startsWith("localhost") ||
            host.startsWith("127.0.0.1") ||
            host.startsWith("10.0.2.2")
        // "/uploads/..." solo lo sirve nuestro backend: si la foto vive ahí se
        // reescribe SIEMPRE al host del backend de la app (el navegador del
        // admin suele quemar "localhost" o una IP de la LAN, inalcanzables
        // desde el emulador/teléfono). Las URLs de Unsplash/Supabase/Railway
        // no tienen ese prefijo y se dejan igual.
        if (afterHost.startsWith("uploads/") || isLoopback) {
            return "$origin/$afterHost"
        }
    }
    return url
}

/** Igual que [resolveBackendImageUrl] pero para un CSV de URLs (campo
 *  `photo_urls` de los lugares). */
fun resolveBackendImageCsv(raw: String?): String {
    val csv = raw?.trim().orEmpty()
    if (csv.isEmpty()) return ""
    return csv.split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .joinToString(",") { resolveBackendImageUrl(it) }
}
