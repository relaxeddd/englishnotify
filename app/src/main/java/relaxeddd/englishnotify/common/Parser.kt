package relaxeddd.englishnotify.common

import org.json.JSONArray
import org.json.JSONObject
import java.util.ArrayList

fun parseWord(wordJson: JSONObject) : Word {
    val eng = if (wordJson.has(ENG)) (wordJson[ENG] as String) else ""
    val rus = if (wordJson.has(RUS)) (wordJson[RUS] as String) else ""
    val transcription = if (wordJson.has(TRANSCRIPTION)) (wordJson[TRANSCRIPTION] as String) else ""
    val tags = ArrayList<String>()

    if (wordJson.has(TAGS) && wordJson[TAGS] is JSONArray) {
        val jsonArray = wordJson[TAGS] as JSONArray

        for (tagIx in 0 until jsonArray.length()) {
            tags.add(jsonArray[tagIx].toString())
        }
    }

    val sampleEng = if (wordJson.has(SAMPLE_ENG)) (wordJson[SAMPLE_ENG] as String) else ""
    val sampleRus = if (wordJson.has(SAMPLE_RUS)) (wordJson[SAMPLE_RUS] as String) else ""
    val v2 = if (wordJson.has(V2)) (wordJson[V2] as String) else ""
    val v3 = if (wordJson.has(V3)) (wordJson[V3] as String) else ""
    val timestamp = System.currentTimeMillis()

    return Word(eng, rus, transcription, tags, sampleEng, sampleRus, v2, v3, timestamp)
}