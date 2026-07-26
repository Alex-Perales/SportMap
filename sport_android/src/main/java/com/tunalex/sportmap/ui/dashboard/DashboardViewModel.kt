package com.tunalex.sportmap.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tunalex.sportmap.data.local.dao.DayOfWeekCount
import com.tunalex.sportmap.data.local.dao.HourCount
import com.tunalex.sportmap.data.local.dao.SportCount
import com.tunalex.sportmap.data.local.entity.AdEntity
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import com.tunalex.sportmap.data.local.entity.UserEntity
import com.tunalex.sportmap.data.repository.AppRepository
import com.tunalex.sportmap.data.repository.UserPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.TimeZone

/** Meta semanal asumida para calcular el "% de meta cumplida". No hay una
 * meta configurable por usuario todavía; es un número fijo razonable. */
private const val WEEKLY_GOAL_SESSIONS = 5

data class DashboardUiState(
    val user: UserEntity? = null,
    val totalKm: Double = 0.0,
    val placesVisited: Int = 0,
    val nextReservation: ReservationEntity? = null,
    val hasAnyReservation: Boolean = false,
    val sessionsThisWeek: Int = 0,
    val sportBreakdown: List<SportCount> = emptyList(),
    val weeklyPattern: List<DayOfWeekCount> = emptyList(),
    val hourlyPattern: List<HourCount> = emptyList(),
    val currentStreakDays: Int = 0,
    val weeklyGoalPercent: Int = 0,
    val favoriteSport: String? = null,
    val ads: List<AdEntity> = emptyList()
)

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val repo: AppRepository,
    private val prefs: UserPreferences
) : ViewModel() {

    val state: StateFlow<DashboardUiState> = prefs.currentUserId.flatMapLatest { id ->
        if (id <= 0L) flowOf(DashboardUiState())
        else {
            val core = combine(
                repo.observeUser(id),
                repo.observeTotalKm(id),
                repo.observePlacesVisited(id),
                repo.observeNextReservation(id)
            ) { user, km, places, next -> CoreInfo(user, km, places, next) }

            val stats = combine(
                repo.observeTotalReservations(id),
                repo.observeSessionsThisWeek(id, currentWeekStartMillis()),
                repo.observeSportBreakdown(id),
                repo.observeWeeklyPattern(id),
                repo.observeHourlyPattern(id)
            ) { total, sessionsThisWeek, sportBreakdown, weekly, hourly ->
                StatsInfo(total, sessionsThisWeek, sportBreakdown, weekly, hourly)
            }

            val extras = combine(
                repo.observeDistinctReservationDates(id),
                repo.observeAds()
            ) { dates, ads -> ExtrasInfo(dates, ads) }

            combine(core, stats, extras) { c, s, e ->
                DashboardUiState(
                    user = c.user,
                    totalKm = c.km,
                    placesVisited = c.places,
                    nextReservation = c.next,
                    hasAnyReservation = s.total > 0,
                    sessionsThisWeek = s.sessionsThisWeek,
                    sportBreakdown = s.sportBreakdown,
                    weeklyPattern = s.weekly,
                    hourlyPattern = s.hourly,
                    currentStreakDays = computeStreak(e.distinctDates),
                    weeklyGoalPercent = (s.sessionsThisWeek * 100 / WEEKLY_GOAL_SESSIONS).coerceAtMost(100),
                    favoriteSport = s.sportBreakdown.maxByOrNull { it.count }?.sportType,
                    ads = e.ads
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    fun togglePremium() {
        val current = state.value.user ?: return
        viewModelScope.launch {
            repo.setPremium(current.id, !current.isPremium)
        }
    }

    private data class CoreInfo(
        val user: UserEntity?,
        val km: Double,
        val places: Int,
        val next: ReservationEntity?
    )

    private data class StatsInfo(
        val total: Int,
        val sessionsThisWeek: Int,
        val sportBreakdown: List<SportCount>,
        val weekly: List<DayOfWeekCount>,
        val hourly: List<HourCount>
    )

    private data class ExtrasInfo(
        val distinctDates: List<Long>,
        val ads: List<AdEntity>
    )
}

/** Medianoche UTC del lunes de la semana actual (en hora local), para que
 * coincida con cómo se guarda `date` en las reservas (medianoche UTC del
 * día calendario elegido). */
private fun currentWeekStartMillis(): Long {
    val local = Calendar.getInstance()
    val dayOfWeek = local.get(Calendar.DAY_OF_WEEK) // 1=domingo .. 7=sábado
    val daysSinceMonday = (dayOfWeek + 5) % 7 // lunes=0, martes=1, ..., domingo=6
    local.add(Calendar.DAY_OF_YEAR, -daysSinceMonday)
    val year = local.get(Calendar.YEAR)
    val month = local.get(Calendar.MONTH)
    val day = local.get(Calendar.DAY_OF_MONTH)
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(year, month, day, 0, 0, 0)
    }.timeInMillis
}

/** Racha de días seguidos (hasta hoy) con al menos una reserva. Ignora
 * fechas futuras: reservar algo para dentro de dos semanas no cuenta como
 * "actividad" de hoy. */
private fun computeStreak(distinctDatesDesc: List<Long>): Int {
    val oneDayMs = 86_400_000L
    val todayUtcDay = run {
        val local = Calendar.getInstance()
        val year = local.get(Calendar.YEAR)
        val month = local.get(Calendar.MONTH)
        val day = local.get(Calendar.DAY_OF_MONTH)
        Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
            clear()
            set(year, month, day, 0, 0, 0)
        }.timeInMillis
    }

    val pastDates = distinctDatesDesc.filter { it <= todayUtcDay }
    if (pastDates.isEmpty()) return 0

    var expected = if (pastDates.first() == todayUtcDay) todayUtcDay else todayUtcDay - oneDayMs
    if (pastDates.first() != todayUtcDay && pastDates.first() != todayUtcDay - oneDayMs) return 0

    var streak = 0
    for (d in pastDates) {
        when {
            d == expected -> {
                streak++
                expected -= oneDayMs
            }
            d < expected -> return streak
        }
    }
    return streak
}
