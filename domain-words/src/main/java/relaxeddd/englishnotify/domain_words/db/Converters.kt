package relaxeddd.englishnotify.domain_words.db

import androidx.room.TypeConverter

internal class ConverterListStr {

    @TypeConverter
    fun fromString(content: String?): List<String> {
        return if (content?.isNotEmpty() == true) content.split(";") else ArrayList()
    }

    @TypeConverter
    fun listToString(list: List<String>?): String {
        return list?.joinToString(";") ?: ""
    }
}
