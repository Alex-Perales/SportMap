package com.tunalex.sportmap.data.local

import com.tunalex.sportmap.data.local.entity.MedalEntity
import com.tunalex.sportmap.data.local.entity.PlaceEntity
import com.tunalex.sportmap.data.local.entity.ProductEntity

/**
 * Datos semilla — se cargan la primera vez que arranca la app.
 * Coordenadas centradas en Lima (Miraflores / Magdalena / Surco).
 */
object Seed {

    val PLACES = listOf(
        // ==== Canchas (deportes de campo) ====
        PlaceEntity(
            name = "Cancha Sintética Miraflores",
            sportType = "futbol",
            category = "field",
            lat = -12.1267, lng = -77.0297,
            isPrivate = true,
            description = "Cancha de fútbol 7 sintética con iluminación LED y gradas techadas.",
            services = "seguridad,baños,iluminación,vestuarios,parking",
            photoUrls = "https://images.unsplash.com/photo-1551958219-acbc608c6377?w=800,https://images.unsplash.com/photo-1431324155629-1a6deb1dec8d?w=800",
            rating = 4.7, pricePerHour = 80.0
        ),
        PlaceEntity(
            name = "Polideportivo Magdalena",
            sportType = "voley",
            category = "field",
            lat = -12.0950, lng = -77.0712,
            isPrivate = false,
            description = "Cancha de vóley pública al aire libre, suelo de cemento.",
            services = "baños,iluminación",
            photoUrls = "https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=800",
            rating = 4.0
        ),
        PlaceEntity(
            name = "Piscina Olímpica Campo de Marte",
            sportType = "natacion",
            category = "field",
            lat = -12.0735, lng = -77.0408,
            isPrivate = true,
            description = "Piscina semi-olímpica con 6 carriles. Clases y horarios libres.",
            services = "seguridad,baños,vestuarios,duchas",
            photoUrls = "https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800",
            rating = 4.5, pricePerHour = 25.0
        ),
        PlaceEntity(
            name = "Cancha Vóley San Isidro",
            sportType = "voley",
            category = "field",
            lat = -12.0976, lng = -77.0365,
            isPrivate = true,
            description = "Cancha de vóley techada con piso de duela.",
            services = "seguridad,baños,iluminación,parking",
            photoUrls = "https://images.unsplash.com/photo-1576267423048-15c0040fec78?w=800",
            rating = 4.6, pricePerHour = 50.0
        ),

        // ==== Trayectos (running / ciclismo) ====
        PlaceEntity(
            name = "Malecón de Miraflores",
            sportType = "correr",
            category = "trayecto",
            lat = -12.1311, lng = -77.0319,
            isPrivate = false,
            description = "Ruta costera de 6 km con vistas al océano. Ideal para running matutino.",
            services = "iluminación,bebederos,baños públicos",
            photoUrls = "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800",
            rating = 4.9,
            airQualityIndex = 38
        ),
        PlaceEntity(
            name = "Ciclovía Costa Verde",
            sportType = "ciclismo",
            category = "trayecto",
            lat = -12.1370, lng = -77.0372,
            isPrivate = false,
            description = "Ciclovía de 12 km a lo largo de la costa. Apta para todos los niveles.",
            services = "iluminación,bebederos",
            photoUrls = "https://images.unsplash.com/photo-1502744688674-c619d1586c9e?w=800",
            rating = 4.8,
            airQualityIndex = 42
        ),
        PlaceEntity(
            name = "Parque Kennedy - Loop Running",
            sportType = "correr",
            category = "trayecto",
            lat = -12.1216, lng = -77.0297,
            isPrivate = false,
            description = "Vuelta de 1.2 km al Parque Kennedy. Perfecta para series cortas.",
            services = "iluminación",
            photoUrls = "https://images.unsplash.com/photo-1502082553048-f009c37129b9?w=800",
            rating = 4.3,
            airQualityIndex = 55
        ),

        // ==== Bienestar ====
        PlaceEntity(
            name = "Bosque El Olivar",
            sportType = "bienestar",
            category = "wellness",
            lat = -12.0998, lng = -77.0389,
            isPrivate = false,
            description = "Bosque de olivos centenarios. Zonas tranquilas para yoga y meditación.",
            services = "baños,bancas,sombra natural",
            photoUrls = "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800",
            rating = 4.9,
            airQualityIndex = 32
        ),
        PlaceEntity(
            name = "Parque de la Amistad - Zen",
            sportType = "bienestar",
            category = "wellness",
            lat = -12.1418, lng = -76.9985,
            isPrivate = false,
            description = "Jardines temáticos y zonas de relajación. Recomendado al amanecer.",
            services = "baños,parking,seguridad",
            photoUrls = "https://images.unsplash.com/photo-1545389336-cf090694435e?w=800",
            rating = 4.6,
            airQualityIndex = 45
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
            imageUrl = "https://images.unsplash.com/photo-1559625481-3654d6f0d0e6?w=800",
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
