# SportMap

Aplicación Android para encontrar canchas, rutas de entrenamiento y espacios de bienestar cerca de ti. Reserva, entrena y mejora día a día.

## Stack tecnológico

- **Lenguaje:** Kotlin 2.0
- **UI:** Jetpack Compose + Material 3
- **Base de datos:** Room (local)
- **Navegación:** Navigation Compose
- **Mapas:** OpenStreetMap (osmdroid) — gratis, sin API key
- **Imágenes:** Coil
- **Preferencias:** DataStore
- **Build:** Gradle Kotlin DSL + Version Catalog (`libs.versions.toml`)
- **minSdk:** 24 (Android 7.0)
- **targetSdk / compileSdk:** 35

## Cómo abrir el proyecto

1. Abre **Android Studio** (Hedgehog o superior).
2. `File` → `Open` → selecciona la carpeta `SportMap`.
3. Espera a que termine el **Gradle Sync** (descarga dependencias).
4. Conecta un emulador o dispositivo físico.
5. Pulsa ▶ **Run 'app'**.

## ✅ Mapas: cero configuración

A diferencia de Google Maps, **osmdroid no requiere API key, ni cuenta, ni tarjeta**. Las dependencias se descargan automáticamente al hacer Gradle Sync. Solo abre el proyecto y compila — el mapa funciona desde el primer arranque.

osmdroid usa los tiles de OpenStreetMap (los mismos que usa Wikipedia). Necesita conexión a internet para descargar los tiles, que luego se cachean en disco.

## Módulos de la app

1. **Autenticación** (Login / Registro) — Local, contraseñas hasheadas con SHA-256.
2. **Dashboard** — Saludo personalizado, km recorridos, lugares visitados, próxima reserva, sección Premium, FAB para empezar actividad.
3. **Mapa** — Filtros por deporte (chips), marcadores diferenciados (canchas / trayectos / bienestar), polylines verdes para rutas, polilíneas punteadas para zonas zen, badge de calidad de aire (AQI).
4. **Detalle de lugar y reservas** — Galería de fotos, descripción, servicios, formulario de reserva con DatePicker / TimePicker.
5. **Tienda** — Banner promocional, catálogo en grid, ficha de producto con tallas y cantidad, carrito con checkout.
6. **Configuración** — Modo oscuro, GPS, perfil, distrito, medallas, suscripción Pro, cerrar sesión, eliminar cuenta, ayuda y FAQ, acerca de.

## Estructura del proyecto

```
SportMap/
├── app/
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/tunalex/sportmap/
│   │   │   ├── MainActivity.kt
│   │   │   ├── SportMapApp.kt          # Application class
│   │   │   ├── data/
│   │   │   │   ├── local/              # Room: entities, DAOs, Database
│   │   │   │   └── repository/         # AuthRepo, AppRepo, UserPreferences
│   │   │   ├── navigation/             # NavGraph, rutas, BottomNav
│   │   │   ├── ui/
│   │   │   │   ├── auth/               # Login, SignUp
│   │   │   │   ├── dashboard/
│   │   │   │   ├── map/                # MapScreen con osmdroid
│   │   │   │   ├── place/
│   │   │   │   ├── store/              # Store, ProductDetail, Cart
│   │   │   │   ├── settings/           # Settings, Medals, Premium, EditProfile
│   │   │   │   ├── components/         # Reusables (BrandLogo)
│   │   │   │   └── theme/              # Color, Theme, Type
│   │   │   └── viewmodel/              # ViewModelFactory
│   │   └── res/                        # strings, themes, drawables, mipmap
│   └── build.gradle.kts
├── gradle/
│   ├── libs.versions.toml              # Version catalog
│   └── wrapper/
├── settings.gradle.kts
└── build.gradle.kts                    # Top-level
```

## Datos semilla

Al primer arranque la app carga automáticamente:
- **9 lugares** en Lima (Miraflores, San Isidro, Magdalena, Surco)
- **8 productos** en la tienda (calzado, balones, ropa, accesorios)
- **8 medallas** por usuario (al registrarse)

## Paleta de colores

Basada en degradados de azul:

| Nombre | HEX | Uso |
|--------|-----|-----|
| Sky Light | `#A8D5F2` | Acentos suaves |
| Blue Light | `#7FB8F5` | Estados secundarios |
| Blue Medium | `#3B82F6` | Iconos |
| Blue Vibrant | `#2563EB` | Primario (botones, links) |
| Indigo Deep | `#3730D9` | Gradientes profundos, modo oscuro |

## Atribución

Los mapas usan datos de © OpenStreetMap contributors, disponibles bajo la [Open Database License (ODbL)](https://www.openstreetmap.org/copyright). La atribución se muestra en pantalla en la parte inferior izquierda del mapa, como exige la licencia.

## Próximos pasos sugeridos

- Integrar pasarela de pago real (Culqi / Stripe) para Premium y tienda.
- Sustituir cabecera del Dashboard por API de clima.
- Conectar API de calidad de aire (Open-Meteo, IQAir) para AQI real.
- Sustituir Auth local por Firebase / backend propio.
- Notificaciones push (Firebase Messaging).

---

**Package:** `com.tunalex.sportmap` · **Versión:** 1.0
