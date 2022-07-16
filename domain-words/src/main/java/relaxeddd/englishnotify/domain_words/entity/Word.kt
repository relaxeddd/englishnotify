package relaxeddd.englishnotify.domain_words.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import relaxeddd.englishnotify.common.TRAINING_ENG_TO_RUS
import relaxeddd.englishnotify.common.TRAINING_RUS_TO_ENG
import relaxeddd.englishnotify.domain_words.utils.WORDS_TABLE
import kotlin.math.min

@Entity(tableName = WORDS_TABLE)
@Keep
data class Word(

    @PrimaryKey
    val id: String = "",
    var eng: String = "",
    var rus: String = "",
    var transcription: String = "",
    var tags: List<String> = ArrayList(),
    val sampleEng: String = "",
    val sampleRus: String = "",
    val v2: String = "",
    val v3: String = "",
    var timestamp: Long = 0L,
    var isDeleted: Boolean = false,
    var learnStage: Int = 0,
    var type: String = "",
    var isCreatedByUser: Boolean = true,
    var isOwnCategory: Boolean = false,
    var level: Int = 0,
    var learnStageSecondary: Int = 0
) {
    constructor(word: Word) : this(word.id,  word.eng, word.rus, word.transcription, word.tags, word.sampleEng, word.sampleRus,
        word.v2, word.v3, word.timestamp, word.isDeleted, word.learnStage, word.type, word.isCreatedByUser,
        word.isOwnCategory, word.level, word.learnStageSecondary)

    fun isLearned(isEnabledSecondaryProgress: Boolean, learnStageMax: Int) : Boolean {
        return if (isEnabledSecondaryProgress) learnStage >= learnStageMax && learnStageSecondary >= learnStageMax else learnStage >= learnStageMax
    }

    fun getLearnProgress(learnStageMax: Int) : Int {
        val progress = min((learnStage.toFloat() / learnStageMax * 100).toInt(), 100)
        return if (progress == 0) 2 else progress
    }

    fun getLearnProgressSecondary(learnStageMax: Int) : Int {
        val progress = min((learnStageSecondary.toFloat() / learnStageMax * 100).toInt(), 100)
        return if (progress == 0) 2 else progress
    }

    fun isLearnedForTraining(isEnabledSecondaryProgress: Boolean, trainingType: Int, learnStageMax: Int) : Boolean {
        return when (trainingType) {
            TRAINING_ENG_TO_RUS -> {
                learnStage >= learnStageMax
            }
            TRAINING_RUS_TO_ENG -> {
                if (isEnabledSecondaryProgress) learnStageSecondary >= learnStageMax else learnStage >= learnStageMax
            }
            else /*MIXED*/ -> {
                if (isEnabledSecondaryProgress) learnStage >= learnStageMax && learnStageSecondary >= learnStageMax else learnStage >= learnStageMax
            }
        }
    }
}
