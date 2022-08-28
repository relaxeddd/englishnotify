package relaxeddd.englishnotify.preferences

import kotlinx.coroutines.flow.StateFlow
import relaxeddd.englishnotify.preferences.models.NotificationRepeatTime

interface Preferences {

    fun isNotificationsEnabled(): Boolean
    fun setNotificationsEnabled(value: Boolean)

    fun isPrivacyPolicyConfirmed(): Boolean
    fun setPrivacyPolicyConfirmed(isConfirmed: Boolean)

    fun isDefaultWordsLoaded(): Boolean
    fun setDefaultWordsLoaded(value: Boolean)

    fun isBottomNavigation(): Boolean
    fun setBottomNavigation(value: Boolean)

    fun isCheckLearnedWords(): Boolean
    fun setCheckLearnedWords(value: Boolean)

    fun isListeningTraining(): Boolean
    fun setListeningTraining(value: Boolean)

    fun isEnabledSecondaryProgress(): Boolean
    fun setEnabledSecondaryProgress(value: Boolean)

    fun isHearAnswer(): Boolean
    fun setHearAnswer(value: Boolean)

    fun isShowProgressInTraining(): Boolean
    fun setShowProgressInTraining(value: Boolean)

    fun isShowVoiceInput(): Boolean
    fun setShowVoiceInput(value: Boolean)

    fun getSelectedLocaleWord(): Int
    fun setSelectedLocaleWord(value: Int)

    fun getTrueAnswersToLearn(): Int
    fun setTrueAnswersToLearn(value: Int)

    fun getNotificationLearnPoints(): Int
    fun setNotificationLearnPoints(value: Int)

    fun getSelectedLocaleTranslation(): Int
    fun setSelectedLocaleTranslation(value: Int)

    fun getSelectedLocaleTraining(): Int
    fun setSelectedLocaleTraining(value: Int)

    fun getStartFragmentId() : Int?
    fun setStartFragmentId(value : Int)

    val learnLanguageTypeFlow: StateFlow<Int>
    fun getLearnLanguageType(): Int
    fun setLearnLanguageType(type : Int)

    fun getDictionaryTabPosition(): Int
    fun setDictionaryTabPosition(type : Int)

    fun getLastOwnCategory(): String
    fun setLastOwnCategory(pushToken : String)

    fun getSortByType(): String
    fun setSortByType(sortType : String)

    fun getNotificationsView() : Int?
    fun setNotificationsView(viewType : Int)

    fun isPatchNotesViewed(version: String): Boolean
    fun setPatchNotesViewed(version: String)

    fun getStartHour(): Int
    fun setStartHour(count : Int)

    fun getDurationHours(): Int
    fun setDurationHours(count : Int)

    val selectedCategoryFlow: StateFlow<String>
    fun getSelectedCategory(): String
    fun setSelectedCategory(string : String)

    fun getTrainingCategory(): String
    fun setTrainingCategory(string : String)

    fun getTrainingLanguage(): Int
    fun setTrainingLanguage(value : Int)

    fun isShowOnlyOneNotification(): Boolean
    fun setShowOnlyOneNotification(value: Boolean)

    fun isOngoingNotification(): Boolean
    fun setOngoingNotification(value: Boolean)

    fun getAppThemeType(): Int
    fun setAppThemeType(value : Int)

    val notificationsRepeatTimeFlow: StateFlow<NotificationRepeatTime>
    fun getNotificationsRepeatTime() : NotificationRepeatTime
    fun setNotificationsRepeatTime(value : Int)
}
