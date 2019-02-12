package com.example.vchechin.testapp.common

import java.util.ArrayList

fun parseUser(notParsedData: MutableMap<String, Any>) : User {
    val name: String = notParsedData[NAME] as String
    val isCanChangeName = notParsedData[IS_CAN_CHANGE_NAME] as Boolean

    return User(name, isCanChangeName)
}

fun parseWord(notParsedData: MutableMap<String, String>) : Word {
    val eng = if (notParsedData.containsKey(ENG)) (notParsedData[ENG] as String) else ""
    val rus = if (notParsedData.containsKey(RUS)) (notParsedData[RUS] as String) else ""
    val v2 = if (notParsedData.containsKey(V2)) (notParsedData[V2] as String) else ""
    val v3 = if (notParsedData.containsKey(V3)) (notParsedData[V3] as String) else ""
    val transcription = if (notParsedData.containsKey(TRANSCRIPTION)) (notParsedData[TRANSCRIPTION] as String) else ""
    val tagsList = notParsedData[TAGS]?.split(",") ?: ArrayList()
    val samplesValues = notParsedData[SAMPLES]?.split(",") ?: ArrayList()
    val samples = HashMap<String, String>()

    for (samplesValue in samplesValues) {
        val sample = samplesValue.split(":")

        if (sample.size > 1) {
            samples[sample[0]] = sample[1]
        }
    }

    return Word(eng, rus, v2, v3, transcription, tagsList, samples)
}