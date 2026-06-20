package com.tunalex.sportmap

import android.app.Application
import com.tunalex.sportmap.data.local.Seed
import com.tunalex.sportmap.data.local.SportMapDatabase
import com.tunalex.sportmap.data.remote.DirectionsRepository
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.AuthRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Application class — punto único de inicialización.
 * Expone instancias singletons (DB, repos, prefs) que los ViewModels reciben vía AppContainer.
 */
class SportMapApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        // Seed data — IGNORE strategy skips rows with existing PKs, inserts new ones
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            container.database.placeDao().insertAll(Seed.PLACES)
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
    override val authRepository: AuthRepository = AuthRepository(
        userDao = database.userDao(),
        medalDao = database.medalDao(),
        prefs = userPreferences
    )
    override val appRepository: AppRepository = AppRepository(
        userDao = database.userDao(),
        activityDao = database.activityDao(),
        placeDao = database.placeDao(),
        reservationDao = database.reservationDao(),
        productDao = database.productDao(),
        cartDao = database.cartDao(),
        medalDao = database.medalDao()
    )
    override val directionsRepository: DirectionsRepository = DirectionsRepository()
}
