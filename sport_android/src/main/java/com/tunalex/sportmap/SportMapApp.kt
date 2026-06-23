package com.tunalex.sportmap

import android.app.Application
import com.tunalex.sportmap.data.local.Seed
import com.tunalex.sportmap.data.local.SportMapDatabase
import com.tunalex.sportmap.data.remote.DirectionsRepository
import com.tunalex.sportmap.data.remote.RetrofitClient
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.AuthRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SportMapApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            // Cargar lugares desde el backend (actualiza el caché Room)
            container.appRepository.syncPlacesFromBackend()

            // Seed local de lugares si Room está vacío
            container.database.placeDao().insertAll(Seed.PLACES)

            // Cargar productos desde el backend
            container.appRepository.syncProductsFromBackend()

            // Seed local de productos si Room está vacío
            val productCount = container.database.productDao().count()
            if (productCount == 0) {
                container.database.productDao().insertAll(Seed.PRODUCTS)
            }
        }
    }
}

interface AppContainer {
    val database: SportMapDatabase
    val authRepository: AuthRepository
    val appRepository: AppRepository
    val userPreferences: UserPreferences
    val directionsRepository: DirectionsRepository
}

class DefaultAppContainer(app: Application) : AppContainer {
    override val database: SportMapDatabase = SportMapDatabase.getInstance(app)
    override val userPreferences: UserPreferences = UserPreferences(app)

    private val api = RetrofitClient.api

    override val authRepository: AuthRepository = AuthRepository(
        userDao = database.userDao(),
        medalDao = database.medalDao(),
        prefs = userPreferences,
        api = api
    )
    override val appRepository: AppRepository = AppRepository(
        userDao = database.userDao(),
        activityDao = database.activityDao(),
        placeDao = database.placeDao(),
        reservationDao = database.reservationDao(),
        productDao = database.productDao(),
        cartDao = database.cartDao(),
        medalDao = database.medalDao(),
        api = api,
        prefs = userPreferences
    )
    override val directionsRepository: DirectionsRepository = DirectionsRepository()
}
