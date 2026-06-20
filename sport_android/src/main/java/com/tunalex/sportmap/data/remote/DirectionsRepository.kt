package com.tunalex.sportmap.data.remote

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

// Valhalla is a production-grade OSM routing engine — no API key required
private const val VALHALLA_URL = "https://valhalla1.openstreetmap.de/route"

data class RouteResult(
    val points: List<LatLng>,
    val distanceText: String,
    val durationText: String,
    val distanceMeters: Int,
    val durationSeconds: Int,
    val steps: List<RouteStep>
)

data class RouteStep(
    val instruction: String,
    val distanceText: String
)

class DirectionsRepository {

    suspend fun getRoute(origin: LatLng, destination: LatLng): Result<RouteResult> =
        withContext(Dispatchers.IO) {
            runCatching {
                // Valhalla POST body — costing "auto" follows real roads only
                val body = buildRequestBody(origin, destination)
                val json = postJson(VALHALLA_URL, body)
                parse(json)
            }
        }

    private fun buildRequestBody(origin: LatLng, dest: LatLng): String =
        """{"locations":[{"lon":${origin.longitude},"lat":${origin.latitude},"type":"break"},{"lon":${dest.longitude},"lat":${dest.latitude},"type":"break"}],"costing":"auto","directions_options":{"language":"es-ES","units":"kilometers"}}"""

    private fun postJson(urlStr: String, body: String): String {
        val conn = URL(urlStr).openConnection() as HttpURLConnection
        conn.connectTimeout = 15_000
        conn.readTimeout  = 15_000
        conn.requestMethod = "POST"
        conn.doOutput = true
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        conn.setRequestProperty("Accept", "application/json")
        conn.setRequestProperty("User-Agent", "SportMapApp/1.0 Android")

        conn.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(body) }

        return try {
            val code = conn.responseCode
            if (code == HttpURLConnection.HTTP_OK) {
                conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            } else {
                val err = conn.errorStream?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                throw IOException("No se pudo calcular la ruta (código $code)")
            }
        } finally {
            conn.disconnect()
        }
    }

    private fun parse(json: String): RouteResult {
        val trip    = JSONObject(json).getJSONObject("trip")
        val summary = trip.getJSONObject("summary")
        val leg     = trip.getJSONArray("legs").getJSONObject(0)

        val distanceKm  = summary.getDouble("length")
        val durationSec = summary.getDouble("time").toInt()

        // Valhalla gives ready-made Spanish turn-by-turn instructions
        val steps = buildList {
            val maneuvers = leg.getJSONArray("maneuvers")
            for (i in 0 until maneuvers.length()) {
                val m = maneuvers.getJSONObject(i)
                val instruction = m.optString("instruction", "").trim()
                val lengthKm    = m.getDouble("length")
                if (instruction.isNotBlank()) {
                    add(RouteStep(instruction, formatDistance((lengthKm * 1000).toInt())))
                }
            }
        }

        // Valhalla encodes the shape with precision 6 (1 × 10⁻⁶ degrees)
        val points = decodePoly6(leg.getString("shape"))

        return RouteResult(
            points        = points,
            distanceText  = "${"%.1f".format(distanceKm)} km",
            durationText  = formatDuration(durationSec),
            distanceMeters  = (distanceKm * 1000).toInt(),
            durationSeconds = durationSec,
            steps         = steps
        )
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun formatDistance(meters: Int): String = when {
        meters >= 1000 -> "${"%.1f".format(meters / 1000.0)} km"
        else -> "$meters m"
    }

    private fun formatDuration(seconds: Int): String {
        val mins = seconds / 60
        return when {
            mins >= 60 -> "${mins / 60} h ${mins % 60} min"
            mins == 0  -> "< 1 min"
            else       -> "$mins min"
        }
    }

    // Polyline decoder for precision 6 (Valhalla default)
    private fun decodePoly6(encoded: String): List<LatLng> {
        val poly  = mutableListOf<LatLng>()
        var index = 0
        val len   = encoded.length
        var lat   = 0
        var lng   = 0
        while (index < len) {
            var b: Int; var shift = 0; var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            lat += if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            shift = 0; result = 0
            do {
                b = encoded[index++].code - 63
                result = result or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            lng += if ((result and 1) != 0) (result shr 1).inv() else result shr 1
            poly.add(LatLng(lat / 1E6, lng / 1E6))
        }
        return poly
    }
}
