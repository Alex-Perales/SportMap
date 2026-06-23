"""Seed de datos iniciales: lugares y productos que deben coincidir con Android Seed.kt."""
import time

NOW = 1686700000000  # timestamp fijo para seed

PLACES = [
    (1, "Cancha Fútbol San Borja",     "futbol",    "field",    -12.0856, -77.0268, True,  "Cancha de fútbol 11 en San Borja con césped sintético de alta calidad e iluminación.",       "seguridad,baños,iluminación,vestuarios,parking",             "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800", 4.6, 120.0, 48),
    (2, "Club Tenis San Borja",         "tenis",     "field",    -12.0945, -77.0315, True,  "4 canchas de tenis de polvo de ladrillo. Clases con instructores certificados.",              "seguridad,baños,vestuarios,cafetería,parking",                "https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=800", 4.8,  60.0, 44),
    (3, "Cancha Vóley San Borja",       "voley",     "field",    -12.0982, -77.0201, True,  "Cancha municipal de vóley con red reglamentaria. Reserva tu hora de 6am a 10pm.",           "iluminación,baños,seguridad",                                 "https://images.unsplash.com/photo-1612872087720-bb876e2e67d1?w=800", 4.1,  40.0, 50),
    (4, "Circuito Mágico del Agua",     "bienestar", "wellness", -12.0712, -77.0102, True,  "Parque con fuentes interactivas y senderos. Ideal para caminatas y relajación.",             "baños,seguridad,iluminación,bancas",                          "https://images.unsplash.com/photo-1519331379826-f10be5486c6f?w=800", 4.7,  10.0, 52),
    (5, "Parque El Olivar",             "correr",    "trayecto", -12.0654, -77.0045, True,  "Circuito de running de 2.5 km entre olivos centenarios. Superficie pisada y nivelada.",     "iluminación,bebederos,baños",                                 "https://images.unsplash.com/photo-1476480862126-209bfaa8edc8?w=800", 4.9,   8.0, 30),
    (6, "Cancha Running La Victoria",   "correr",    "trayecto", -12.0758, -77.0156, True,  "Pista atlética de 400m con tartan. Abierta al público en horario de mañana.",               "baños,vestuarios,iluminación",                                "https://images.unsplash.com/photo-1571902943202-507ec2618e8f?w=800", 4.4,  15.0, 55),
    (7, "Costa Verde",                  "ciclismo",  "trayecto", -12.1246, -77.0291, True,  "Tramo costero con ciclovía y carril de running. Vistas panorámicas al mar.",               "bebederos,estacionamiento bicicletas,iluminación",            "https://images.unsplash.com/photo-1502744688674-c619d1586c9e?w=800", 4.8,  10.0, 35),
    (8, "Parque Central Miraflores",    "bienestar", "wellness", -12.1302, -77.0248, True,  "Parque céntrico con áreas verdes para yoga, tai chi y meditación al aire libre.",           "baños,bancas,iluminación,seguridad",                          "https://images.unsplash.com/photo-1545389336-cf090694435e?w=800", 4.6,   5.0, 40),
    (9, "Cancha Natación Miraflores",   "natacion",  "field",    -12.1178, -77.0340, True,  "Piscina olímpica de 50m con 8 carriles. Programas de natación para todas las edades.",     "seguridad,baños,vestuarios,duchas,cafetería",                 "https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800", 4.7,  30.0, 38),
    (10, "Jockey Club",                 "tenis",     "field",    -12.0956, -77.0385, True,  "8 canchas de tenis de primer nivel (polvo y dura). Sede de torneos ATP challenger.",        "seguridad,baños,vestuarios,restaurante,parking valet",        "https://images.unsplash.com/photo-1554068865-24cecd4e34b8?w=800", 4.9,  90.0, 42),
    (11, "Parque Botánico San Isidro",  "bienestar", "wellness", -12.0901, -77.0341, True,  "Reserva verde de 5 hectáreas. Senderos de meditación y áreas de yoga.",                    "bancas,sombra natural,bebederos,seguridad",                   "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800", 4.8,   5.0, 28),
    (12, "Zona Deportiva San Isidro",   "futbol",    "field",    -12.0875, -77.0412, True,  "Complejo deportivo con canchas de fútbol 7 y 11. Reserva con anticipación.",               "baños,iluminación,tribuna,estacionamiento,vestuarios",        "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=800", 4.3,  20.0, 46),
]

PRODUCTS = [
    ("Zapatillas Running Pro",      "Zapatillas livianas con amortiguación responsiva. Perfectas para running diario.",      249.90, "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",  "calzado",      "38,39,40,41,42,43", 30, True,  15),
    ("Balón Fútbol Profesional",    "Balón cosido a mano. Reglamentario FIFA, talla 5.",                                      89.90, "https://images.unsplash.com/photo-1614632537190-23e4146777db?w=800",  "balones",      "5",                 50, False,  0),
    ("Botella Térmica 750ml",       "Acero inoxidable. Mantiene temperatura por 12 horas.",                                   59.00, "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=800",  "hidratación",  "750ml",             80, False,  0),
    ("Bandas de Resistencia x5",    "Set de 5 bandas con diferentes intensidades. Incluye bolsa.",                            45.00, "https://images.unsplash.com/photo-1598289431512-b97b0917affc?w=800",  "accesorios",   "Único",             40, False,  0),
    ("Antiparras Natación",         "Antiparras anti-empañe con protección UV.",                                              39.90, "https://images.unsplash.com/photo-1530549387789-4c1017266635?w=800",  "accesorios",   "Único",             60, True,  20),
    ("Casco Ciclismo Aero",         "Casco aerodinámico con ventilación. Cumple normativa.",                                 179.00, "https://images.unsplash.com/photo-1618886614638-80e3c103d31a?w=800",  "accesorios",   "S,M,L",             25, False,  0),
    ("Camiseta Dry-Fit",            "Tela técnica que evapora el sudor. Corte deportivo.",                                    69.90, "https://images.unsplash.com/photo-1556906781-9a412961c28c?w=800",  "ropa",         "S,M,L,XL",          70, False,  0),
    ("Smartband Fitness",           "Pulsera con monitor cardíaco, pasos y notificaciones.",                                 199.00, "https://images.unsplash.com/photo-1575311373937-040b8e1fd5b6?w=800",  "accesorios",   "Único",             35, False,  0),
]


async def seed_places_and_products(conn):
    """Inserta lugares y productos si no existen (idempotente)."""
    count = await conn.fetchval("SELECT COUNT(*) FROM places")
    if count < 12:
        # Reset sequence and insert all places with explicit IDs
        await conn.execute("TRUNCATE places RESTART IDENTITY CASCADE")
        for p in PLACES:
            await conn.execute(
                """INSERT INTO places
                   (id, name, sport_type, category, lat, lng, is_private, description,
                    services, photo_urls, rating, price_per_hour, air_quality_index, created_at, updated_at)
                   VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$11,$12,$13,$14,$14)
                   ON CONFLICT (id) DO NOTHING""",
                *p, NOW
            )
        await conn.execute("SELECT setval('places_id_seq', 12)")

    prod_count = await conn.fetchval("SELECT COUNT(*) FROM products")
    if prod_count == 0:
        for p in PRODUCTS:
            await conn.execute(
                """INSERT INTO products
                   (name, description, price, image_url, category, sizes, stock, is_on_sale, discount_percent, created_at, updated_at)
                   VALUES ($1,$2,$3,$4,$5,$6,$7,$8,$9,$10,$10)""",
                *p, NOW
            )
