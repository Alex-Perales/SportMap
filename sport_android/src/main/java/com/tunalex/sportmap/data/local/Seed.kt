package com.tunalex.sportmap.data.local

import com.tunalex.sportmap.data.local.entity.MedalEntity
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.local.entity.ProductEntity

object Seed {

    val PLACES = listOf(
        // ── SAN BORJA ───────────────────────────────────────────────────────
        PlaceEntity(
            id = 1,
            name = "Cancha Fútbol San Borja",
            sportType = "futbol", category = "field",
            lat = -12.0856, lng = -77.0268,
            isPrivate = true,
            description = "Cancha de fútbol 11 en San Borja con césped sintético de alta calidad e iluminación.",
            services = "seguridad,baños,iluminación,vestuarios,parking",
            photoUrls = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800",
            rating = 4.6, pricePerHour = 120.0, airQualityIndex = 48
        ),
        PlaceEntity(
            id = 2,
            name = "Club Tenis San Borja",
            sportType = "tenis", category = "field",
            lat = -12.0945, lng = -77.0315,
            isPrivate = true,
            description = "4 canchas de tenis de polvo de ladrillo. Clases con instructores certificados.",
            services = "seguridad,baños,vestuarios,cafetería,parking",
            photoUrls = "https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=800",
            rating = 4.8, pricePerHour = 60.0, airQualityIndex = 44
        ),
        PlaceEntity(
            id = 3,
            name = "Cancha Vóley San Borja",
            sportType = "voley", category = "field",
            lat = -12.0982, lng = -77.0201,
            isPrivate = true,
            description = "Cancha municipal de vóley con red reglamentaria. Reserva tu hora de 6am a 10pm.",
            services = "iluminación,baños,seguridad",
            photoUrls = "https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=800",
            rating = 4.1, pricePerHour = 40.0, airQualityIndex = 50
        ),
        // ── LA VICTORIA ─────────────────────────────────────────────────────
        PlaceEntity(
            id = 4,
            name = "Circuito Mágico del Agua",
            sportType = "bienestar", category = "wellness",
            lat = -12.0712, lng = -77.0102,
            isPrivate = true,
            description = "Parque con fuentes interactivas y senderos. Ideal para caminatas y relajación.",
            services = "baños,seguridad,iluminación,bancas",
            photoUrls = "https://images.unsplash.com/photo-1519331379826-f10be5486c6f?w=800",
            rating = 4.7, pricePerHour = 10.0, airQualityIndex = 52
        ),
        PlaceEntity(
            id = 5,
            name = "Parque El Olivar",
            sportType = "correr", category = "trayecto",
            lat = -12.0654, lng = -77.0045,
            isPrivate = true,
            description = "Circuito de running de 2.5 km entre olivos centenarios. Superficie pisada y nivelada.",
            services = "iluminación,bebederos,baños",
            photoUrls = "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?w=800",
            rating = 4.9, pricePerHour = 8.0, airQualityIndex = 30
        ),
        PlaceEntity(
            id = 6,
            name = "Cancha Running La Victoria",
            sportType = "correr", category = "trayecto",
            lat = -12.0758, lng = -77.0156,
            isPrivate = true,
            description = "Pista atlética de 400m con tartan. Abierta al público en horario de mañana.",
            services = "baños,vestuarios,iluminación",
            photoUrls = "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800",
            rating = 4.4, pricePerHour = 15.0, airQualityIndex = 55
        ),
        // ── MIRAFLORES ──────────────────────────────────────────────────────
        PlaceEntity(
            id = 7,
            name = "Costa Verde",
            sportType = "ciclismo", category = "trayecto",
            lat = -12.1246, lng = -77.0291,
            isPrivate = true,
            description = "Tramo costero con ciclovía y carril de running. Vistas panorámicas al mar.",
            services = "bebederos,estacionamiento bicicletas,iluminación",
            photoUrls = "https://images.unsplash.com/photo-1502744688674-c619d1586c9e?w=800",
            rating = 4.8, pricePerHour = 10.0, airQualityIndex = 35
        ),
        PlaceEntity(
            id = 8,
            name = "Parque Central Miraflores",
            sportType = "bienestar", category = "wellness",
            lat = -12.1302, lng = -77.0248,
            isPrivate = true,
            description = "Parque céntrico con áreas verdes para yoga, tai chi y meditación al aire libre.",
            services = "baños,bancas,iluminación,seguridad",
            photoUrls = "https://images.unsplash.com/photo-1545389336-cf090694435e?w=800",
            rating = 4.6, pricePerHour = 5.0, airQualityIndex = 40
        ),
        PlaceEntity(
            id = 9,
            name = "Cancha Natación Miraflores",
            sportType = "natacion", category = "field",
            lat = -12.1178, lng = -77.0340,
            isPrivate = true,
            description = "Piscina olímpica de 50m con 8 carriles. Programas de natación para todas las edades.",
            services = "seguridad,baños,vestuarios,duchas,cafetería",
            photoUrls = "https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800",
            rating = 4.7, pricePerHour = 30.0, airQualityIndex = 38
        ),
        // ── SAN ISIDRO ──────────────────────────────────────────────────────
        PlaceEntity(
            id = 10,
            name = "Jockey Club",
            sportType = "tenis", category = "field",
            lat = -12.0956, lng = -77.0385,
            isPrivate = true,
            description = "8 canchas de tenis de primer nivel (polvo y dura). Sede de torneos ATP challenger.",
            services = "seguridad,baños,vestuarios,restaurante,parking valet",
            photoUrls = "https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=800",
            rating = 4.9, pricePerHour = 90.0, airQualityIndex = 42
        ),
        PlaceEntity(
            id = 11,
            name = "Parque Botánico San Isidro",
            sportType = "bienestar", category = "wellness",
            lat = -12.0901, lng = -77.0341,
            isPrivate = true,
            description = "Reserva verde de 5 hectáreas. Senderos de meditación y áreas de yoga.",
            services = "bancas,sombra natural,bebederos,seguridad",
            photoUrls = "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800",
            rating = 4.8, pricePerHour = 5.0, airQualityIndex = 28
        ),
        PlaceEntity(
            id = 12,
            name = "Zona Deportiva San Isidro",
            sportType = "futbol", category = "field",
            lat = -12.0875, lng = -77.0412,
            isPrivate = true,
            description = "Complejo deportivo con canchas de fútbol 7 y 11. Reserva con anticipación.",
            services = "baños,iluminación,tribuna,estacionamiento,vestuarios",
            photoUrls = "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800",
            rating = 4.3, pricePerHour = 20.0, airQualityIndex = 46
        )
    )

    val PRODUCTS = listOf(
        ProductEntity(
            name = "Zapatillas Running Pro",
            description = "Zapatillas livianas con amortiguación responsiva. Perfectas para running diario.",
            price = 249.90,
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
            category = "calzado",
            sizes = "38,39,40,41,42,43",
            stock = 30,
            isOnSale = true,
            discountPercent = 15
        ),
        ProductEntity(
            name = "Balón Fútbol Profesional",
            description = "Balón cosido a mano. Reglamentario FIFA, talla 5.",
            price = 89.90,
            imageUrl = "https://images.unsplash.com/photo-1614632537190-23e4146777db?w=800",
            category = "balones",
            sizes = "5",
            stock = 50
        ),
        ProductEntity(
            name = "Botella Térmica 750ml",
            description = "Acero inoxidable. Mantiene temperatura por 12 horas.",
            price = 59.00,
            imageUrl = "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800",
            category = "hidratación",
            sizes = "750ml",
            stock = 80
        ),
        ProductEntity(
            name = "Bandas de Resistencia x5",
            description = "Set de 5 bandas con diferentes intensidades. Incluye bolsa.",
            price = 45.00,
            imageUrl = "https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=800",
            category = "accesorios",
            sizes = "Único",
            stock = 40
        ),
        ProductEntity(
            name = "Antiparras Natación",
            description = "Antiparras anti-empañe con protección UV.",
            price = 39.90,
            imageUrl = "https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800",
            category = "accesorios",
            sizes = "Único",
            stock = 60,
            isOnSale = true,
            discountPercent = 20
        ),
        ProductEntity(
            name = "Casco Ciclismo Aero",
            description = "Casco aerodinámico con ventilación. Cumple normativa.",
            price = 179.00,
            imageUrl = "https://images.unsplash.com/photo-1618886614638-80e3c103d31a?w=800",
            category = "accesorios",
            sizes = "S,M,L",
            stock = 25
        ),
        ProductEntity(
            name = "Camiseta Dry-Fit",
            description = "Tela técnica que evapora el sudor. Corte deportivo.",
            price = 69.90,
            imageUrl = "https://images.unsplash.com/photo-1556906781-9a412961c28c?w=800",
            category = "ropa",
            sizes = "S,M,L,XL",
            stock = 70
        ),
        ProductEntity(
            name = "Smartband Fitness",
            description = "Pulsera con monitor cardíaco, pasos y notificaciones.",
            price = 199.00,
            imageUrl = "https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=800",
            category = "accesorios",
            sizes = "Único",
            stock = 35
        )
    )

    fun medalsForUser(userId: Long): List<MedalEntity> = listOf(
        MedalEntity(userId = userId, name = "Corredor madrugador", description = "Completa 5 carreras antes de las 7am", iconKey = "morning", earned = true, earnedDate = System.currentTimeMillis(), tier = "gold"),
        MedalEntity(userId = userId, name = "Dueño de la cancha", description = "Reserva 10 canchas en un mes", iconKey = "field", earned = true, earnedDate = System.currentTimeMillis(), tier = "silver"),
        MedalEntity(userId = userId, name = "Explorador urbano", description = "Visita 15 lugares deportivos diferentes", iconKey = "explorer", earned = false, tier = "gold"),
        MedalEntity(userId = userId, name = "Ciclista de acero", description = "Acumula 100 km en bicicleta", iconKey = "cycling", earned = true, earnedDate = System.currentTimeMillis(), tier = "bronze"),
        MedalEntity(userId = userId, name = "Sirena", description = "Nada 20 km en piscina", iconKey = "swimming", earned = false, tier = "silver"),
        MedalEntity(userId = userId, name = "Maratonista", description = "Completa una distancia de 42 km en running", iconKey = "marathon", earned = false, tier = "gold"),
        MedalEntity(userId = userId, name = "Yogui", description = "Visita 5 zonas de bienestar", iconKey = "wellness", earned = true, earnedDate = System.currentTimeMillis(), tier = "bronze"),
        MedalEntity(userId = userId, name = "Capitán", description = "Organiza 5 partidos con tu grupo", iconKey = "captain", earned = false, tier = "silver")
    )
}
