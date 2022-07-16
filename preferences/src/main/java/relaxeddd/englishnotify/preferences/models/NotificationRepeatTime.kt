package relaxeddd.englishnotify.preferences.models

enum class NotificationRepeatTime(val valueInMinutes: Long) {

    MINUTES_30(30),
    MINUTES_60(60),
    MINUTES_90(90),
    MINUTES_120(120),
    MINUTES_150(150),
    MINUTES_180(180),
    MINUTES_210(210),
    MINUTES_240(240),
    MINUTES_270(270),
    MINUTES_300(300),
    MINUTES_330(330),
    MINUTES_360(360),
    MINUTES_400(400);

    companion object {

        fun valueOf(ordinal: Int) : NotificationRepeatTime {
            return values().find { it.ordinal == ordinal } ?: MINUTES_60
        }
    }
}
