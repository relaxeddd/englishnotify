package relaxeddd.englishnotify.model.db

import androidx.room.TypeConverter

class ConverterListStr {

    @TypeConverter
    fun fromString(content: String?): List<String> {
        return if (content?.isNotEmpty() == true) content.split(";") else ArrayList()
    }

    @TypeConverter
    fun listToString(list: List<String>?): String {
        return list?.joinToString(";") ?: ""
    }
}