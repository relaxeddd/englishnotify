package relaxeddd.englishnotify.notifications

import java.util.*

fun isNightTime(startHour: Int, durationHours: Int) : Boolean {
    val endHour = if (startHour + durationHours >= 24) startHour + durationHours - 24 else startHour + durationHours
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

    return durationHours != 0 && ((currentHour in startHour until endHour)
            || (startHour + durationHours >= 24 && currentHour < endHour) )
}
