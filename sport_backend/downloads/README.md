# downloads/

Aquí va el APK que la landing page ofrece en el botón **"Descargar APK"**
(ruta pública: `GET /download`).

## Cómo actualizar el APK

1. Genera el APK release firmado en Android Studio
   (`sport_android/app/release/app-release.apk`).
2. Cópialo aquí con el nombre exacto **`sportmap.apk`**:

   ```
   sport_backend/downloads/sportmap.apk
   ```

3. Haz commit y push. Render reconstruye la imagen y sirve el archivo nuevo.

Si `sportmap.apk` no existe, `GET /download` responde 404 con un mensaje
claro (no rompe nada).
