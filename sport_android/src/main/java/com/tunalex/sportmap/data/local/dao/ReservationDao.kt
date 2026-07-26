package com.tunalex.sportmap.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tunalex.sportmap.data.local.entity.ReservationEntity
import kotlinx.coroutines.flow.Flow

data class SportCount(val sportType: String, val count: Int)
data class DayOfWeekCount(val dow: Int, val count: Int)
data class HourCount(val hour: Int, val count: Int)

@Dao
interface ReservationDao {

    @Insert
    suspend fun insert(reservation: ReservationEntity): Long

    @Query("SELECT * FROM reservations WHERE userId = :userId ORDER BY date ASC")
    fun observeByUser(userId: Long): Flow<List<ReservationEntity>>

    @Query("""
        SELECT * FROM reservations
        WHERE userId = :userId AND date >= :now
        ORDER BY date ASC LIMIT 1
    """)
    fun observeNextReservation(userId: Long, now: Long): Flow<ReservationEntity?>

    @Query("DELETE FROM reservations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM reservations WHERE userId = :userId")
    fun observeTotalReservations(userId: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM reservations WHERE userId = :userId AND date >= :weekStartMillis")
    fun observeSessionsThisWeek(userId: Long, weekStartMillis: Long): Flow<Int>

    /** Cuenta reservas por deporte (join con places), para el donut de "Actividad por deporte". */
    @Query("""
        SELECT p.sportType AS sportType, COUNT(*) AS count
        FROM reservations r
        JOIN places p ON r.placeId = p.id
        WHERE r.userId = :userId
        GROUP BY p.sportType
        ORDER BY count DESC
    """)
    fun observeSportBreakdown(userId: Long): Flow<List<SportCount>>

    /** Cuenta reservas por día de la semana (0=domingo..6=sábado), para el gráfico semanal. */
    @Query("""
        SELECT CAST(strftime('%w', date / 1000, 'unixepoch') AS INTEGER) AS dow, COUNT(*) AS count
        FROM reservations
        WHERE userId = :userId
        GROUP BY dow
    """)
    fun observeWeeklyPattern(userId: Long): Flow<List<DayOfWeekCount>>

    /** Cuenta reservas por hora del día (a partir del campo "HH:mm"), para "Horas Más Activas". */
    @Query("""
        SELECT CAST(substr(time, 1, 2) AS INTEGER) AS hour, COUNT(*) AS count
        FROM reservations
        WHERE userId = :userId
        GROUP BY hour
    """)
    fun observeHourlyPattern(userId: Long): Flow<List<HourCount>>

    /** Fechas distintas con reserva (para calcular la racha de días seguidos). */
    @Query("SELECT DISTINCT date FROM reservations WHERE userId = :userId ORDER BY date DESC")
    fun observeDistinctDates(userId: Long): Flow<List<Long>>
}
