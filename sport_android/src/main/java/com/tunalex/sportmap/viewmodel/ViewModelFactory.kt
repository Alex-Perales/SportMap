package com.tunalex.sportmap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.tunalex.sportmap.SportMapApp
import com.tunalex.sportmap.ui.auth.AuthViewModel
import com.tunalex.sportmap.ui.dashboard.DashboardViewModel
import com.tunalex.sportmap.ui.map.MapViewModel
import com.tunalex.sportmap.ui.map.RouteViewModel
import com.tunalex.sportmap.ui.place.PlaceDetailViewModel
import com.tunalex.sportmap.ui.settings.ReservationHistoryViewModel
import com.tunalex.sportmap.ui.settings.SettingsViewModel
import com.tunalex.sportmap.ui.store.StoreViewModel
import com.tunalex.sportmap.ui.store.ProductDetailViewModel
import com.tunalex.sportmap.ui.store.CartViewModel

object SportMapViewModels {

    val Factory = viewModelFactory {
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            AuthViewModel(app.container.authRepository)
        }
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            DashboardViewModel(app.container.appRepository, app.container.userPreferences)
        }
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            MapViewModel(app.container.appRepository)
        }
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            PlaceDetailViewModel(app.container.appRepository, app.container.userPreferences)
        }
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            StoreViewModel(app.container.appRepository, app.container.userPreferences)
        }
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            ProductDetailViewModel(app.container.appRepository, app.container.userPreferences)
        }
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            CartViewModel(app.container.appRepository, app.container.userPreferences)
        }
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            RouteViewModel(app.container.appRepository, app.container.directionsRepository)
        }
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            SettingsViewModel(
                app.container.appRepository,
                app.container.authRepository,
                app.container.userPreferences
            )
        }
        initializer {
            val app = (this[APPLICATION_KEY] as SportMapApp)
            ReservationHistoryViewModel(app.container.appRepository, app.container.userPreferences)
        }
    }
}
