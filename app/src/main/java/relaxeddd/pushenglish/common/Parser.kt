package relaxeddd.pushenglish.common

import java.util.ArrayList

fun parseUser(notParsedData: MutableMap<String, Any>) : User {
    val id = notParsedData[ID] as String
    val name = notParsedData[EMAIL] as String
    val receiveNotifications = notParsedData[RECEIVE_NOTIFICATIONS] as Boolean
    val notificationsTimeType = notParsedData[NOTIFICATIONS_TIME_TYPE] as Int
    val tagsAvailable = if (notParsedData.containsKey(TAGS_AVAILABLE)) {
        (notParsedData[TAGS_AVAILABLE] as String).split(",")
    } else {
        ArrayList()
    }
    val tagsSelected = if (notParsedData.containsKey(TAGS_SELECTED)) {
        (notParsedData[TAGS_SELECTED] as String).split(",")
    } else {
        ArrayList()
    }
    val learnLanguageType = notParsedData[LEARN_LANGUAGE_TYPE] as Int

    return User(
        id,
        name,
        receiveNotifications,
        notificationsTimeType,
        tagsAvailable,
        tagsSelected,
        learnLanguageType
    )
}

fun parseWord(notParsedData: MutableMap<String, String>) : Word {
    val eng = if (notParsedData.containsKey(ENG)) (notParsedData[ENG] as String) else ""
    val rus = if (notParsedData.containsKey(RUS)) (notParsedData[RUS] as String) else ""
    val transcription = if (notParsedData.containsKey(TRANSCRIPTION)) (notParsedData[TRANSCRIPTION] as String) else ""
    val tags = if (notParsedData.containsKey(TAGS)) {
        (notParsedData[TAGS] as String).split(",")
    } else {
        ArrayList()
    }
    val sampleEng = if (notParsedData.containsKey(SAMPLE_ENG)) (notParsedData[SAMPLE_ENG] as String) else ""
    val sampleRus = if (notParsedData.containsKey(SAMPLE_RUS)) (notParsedData[SAMPLE_RUS] as String) else ""
    val v2 = if (notParsedData.containsKey(V2)) (notParsedData[V2] as String) else ""
    val v3 = if (notParsedData.containsKey(V3)) (notParsedData[V3] as String) else ""
    val timestamp = if (notParsedData.containsKey(TIMESTAMP)) (notParsedData[TIMESTAMP]?.toLong()) ?: 0 else 0

    return Word(eng, rus, transcription, tags, sampleEng, sampleRus, v2, v3, timestamp)
}