package relaxeddd.englishnotify.preferences

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import relaxeddd.englishnotify.preferences.models.NotificationRepeatTime
import relaxeddd.englishnotify.preferences.models.NotificationRepeatTime.MINUTES_60
import relaxeddd.englishnotify.preferences.models.SortByType
import relaxeddd.englishnotify.preferences.utils.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Preferences @Inject constructor(context: Context) {

    private val prefs = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)

    fun isNotificationsEnabled() = prefs.getBoolean(NOTIFICATIONS_ENABLED, true)
    fun setNotificationsEnabled(value: Boolean) {
        prefs.edit().putBoolean(NOTIFICATIONS_ENABLED, value).apply()
    }

    fun isPrivacyPolicyConfirmed() = prefs.getBoolean(PRIVACY_POLICY_CONFIRMED, false)
    fun setPrivacyPolicyConfirmed(isConfirmed: Boolean) {
        prefs.edit().putBoolean(PRIVACY_POLICY_CONFIRMED, isConfirmed).apply()
    }

    fun isDefaultWordsLoaded() = prefs.getBoolean(IS_DEFAULT_WORDS_LOADED, false)
    fun setDefaultWordsLoaded(value: Boolean) {
        prefs.edit().putBoolean(IS_DEFAULT_WORDS_LOADED, value).apply()
    }

    fun isBottomNavigation() = prefs.getBoolean(IS_BOTTOM_NAVIGATION, true)
    fun setBottomNavigation(value: Boolean) {
        prefs.edit().putBoolean(IS_BOTTOM_NAVIGATION, value).apply()
    }

    fun isCheckLearnedWords() = prefs.getBoolean(CHECK_LEARNED_WORDS, false)
    fun setCheckLearnedWords(value: Boolean) {
        prefs.edit().putBoolean(CHECK_LEARNED_WORDS, value).apply()
    }

    fun isListeningTraining() = prefs.getBoolean(LISTENING_TRAINING, false)
    fun setListeningTraining(value: Boolean) {
        prefs.edit().putBoolean(LISTENING_TRAINING, value).apply()
    }

    fun isEnabledSecondaryProgress() = prefs.getBoolean(SECONDARY_PROGRESS_ENABLED, false)
    fun setEnabledSecondaryProgress(value: Boolean) {
        prefs.edit().putBoolean(SECONDARY_PROGRESS_ENABLED, value).apply()
    }

    fun isHearAnswer() = prefs.getBoolean(HEAR_ANSWER, false)
    fun setHearAnswer(value: Boolean) {
        prefs.edit().putBoolean(HEAR_ANSWER, value).apply()
    }

    fun isShowProgressInTraining() = prefs.getBoolean(PROGRESS_IN_TRAINING, false)
    fun setShowProgressInTraining(value: Boolean) {
        prefs.edit().putBoolean(PROGRESS_IN_TRAINING, value).apply()
    }

    fun isShowVoiceInput() = prefs.getBoolean(VOICE_INPUT, true)

    fun setShowVoiceInput(value: Boolean) {
        prefs.edit().putBoolean(VOICE_INPUT, value).apply()
    }

    fun getSelectedLocaleWord() = prefs.getInt(SELECTED_LOCALE_WORD, 0)
    fun setSelectedLocaleWord(value: Int) {
        prefs.edit().putInt(SELECTED_LOCALE_WORD, value).apply()
    }

    fun getTrueAnswersToLearn() = prefs.getInt(TRUE_ANSWERS_TO_LEARN, 3)
    fun setTrueAnswersToLearn(value: Int) {
        prefs.edit().putInt(TRUE_ANSWERS_TO_LEARN, value).apply()
    }

    fun getNotificationLearnPoints() = prefs.getInt(LEARN_POINTS_NOTIFICATION, 1)
    fun setNotificationLearnPoints(value: Int) {
        prefs.edit().putInt(LEARN_POINTS_NOTIFICATION, value).apply()
    }

    fun getSelectedLocaleTranslation() = prefs.getInt(SELECTED_LOCALE_TRANSLATION, 1)
    fun setSelectedLocaleTranslation(value: Int) {
        prefs.edit().putInt(SELECTED_LOCALE_TRANSLATION, value).apply()
    }

    fun getSelectedLocaleTraining() = prefs.getInt(SELECTED_LOCALE_TRAINING, 0)
    fun setSelectedLocaleTraining(value: Int) {
        prefs.edit().putInt(SELECTED_LOCALE_TRAINING, value).apply()
    }

    fun getStartFragmentId() : Int? {
        val savedStartFragmentId = prefs.getInt(START_FRAGMENT_ID, UNINITIALIZED_START_FRAGMENT_ID)

        return if (savedStartFragmentId != UNINITIALIZED_START_FRAGMENT_ID) {
            savedStartFragmentId
        } else {
            null
        }
    }
    fun setStartFragmentId(value : Int) {
        prefs.edit().putInt(START_FRAGMENT_ID, value).apply()
    }

    private val _learnLanguageTypeFlow: MutableStateFlow<Int> by lazy {
        MutableStateFlow(getLearnLanguageType())
    }
    val learnLanguageTypeFlow: StateFlow<Int> by lazy {
        _learnLanguageTypeFlow.asStateFlow()
    }
    fun getLearnLanguageType() = prefs.getInt(PUSH_LANGUAGE, TYPE_PUSH_ENGLISH)
    fun setLearnLanguageType(type : Int) {
        prefs.edit().putInt(PUSH_LANGUAGE, type).apply()
        _learnLanguageTypeFlow.value = type
    }

    fun getDictionaryTabPosition() = prefs.getInt(DICTIONARY_TAB_POSITION, 0)
    fun setDictionaryTabPosition(type : Int) {
        prefs.edit().putInt(DICTIONARY_TAB_POSITION, type).apply()
    }

    fun getLastOwnCategory() = prefs.getString(LAST_OWN_CATEGORY, "") ?: ""
    fun setLastOwnCategory(pushToken : String) {
        prefs.edit().putString(LAST_OWN_CATEGORY, pushToken).apply()
    }

    fun getSortByType() = prefs.getString(SORT_BY_TYPE, SortByType.TIME_NEW.name) ?: ""
    fun setSortByType(sortType : String) {
        prefs.edit().putString(SORT_BY_TYPE, sortType).apply()
    }

    fun getNotificationsView() : Int? {
        val savedNotificationViewType = prefs.getInt(NOTIFICATIONS_VIEW, UNINITIALIZED_NOTIFICATION_VIEW_TYPE)
        return if (savedNotificationViewType != UNINITIALIZED_NOTIFICATION_VIEW_TYPE) {
            savedNotificationViewType
        } else {
            null
        }
    }
    fun setNotificationsView(viewType : Int) {
        prefs.edit().putInt(NOTIFICATIONS_VIEW, viewType).apply()
    }

    fun isPatchNotesViewed(version: String) = prefs.getBoolean(version, false)
    fun setPatchNotesViewed(version: String) {
        prefs.edit().putBoolean(version, true).apply()
    }

    fun getStartHour() = prefs.getInt(START_HOUR_OFF, 0)
    fun setStartHour(count : Int) {
        prefs.edit().putInt(START_HOUR_OFF, count).apply()
    }

    fun getDurationHours() = prefs.getInt(DURATION_HOURS_OFF, 0)
    fun setDurationHours(count : Int) {
        prefs.edit().putInt(DURATION_HOURS_OFF, count).apply()
    }

    private val _selectedCategoryFlow: MutableStateFlow<String> by lazy {
        MutableStateFlow(getSelectedCategory())
    }
    val selectedCategoryFlow: StateFlow<String> by lazy {
        _selectedCategoryFlow.asStateFlow()
    }
    fun getSelectedCategory() = prefs.getString(SELECTED_CATEGORY, ALL_APP_WORDS) ?: ""
    fun setSelectedCategory(string : String) {
        prefs.edit().putString(SELECTED_CATEGORY, string).apply()
        _selectedCategoryFlow.value = string
    }

    fun getTrainingCategory() = prefs.getString(TRAINING_CATEGORY, ALL_APP_WORDS) ?: ""
    fun setTrainingCategory(string : String) {
        prefs.edit().putString(TRAINING_CATEGORY, string).apply()
    }

    fun getTrainingLanguage() = prefs.getInt(TRAINING_LANGUAGE, 0)
    fun setTrainingLanguage(value : Int) {
        prefs.edit().putInt(TRAINING_LANGUAGE, value).apply()
    }

    fun isShowOnlyOneNotification() = prefs.getBoolean(IS_SHOW_ONLY_ONE_NOTIFICATION, true)
    fun setShowOnlyOneNotification(value: Boolean) {
        prefs.edit().putBoolean(IS_SHOW_ONLY_ONE_NOTIFICATION, value).apply()
    }

    fun isOngoingNotification() = prefs.getBoolean(IS_ONGOING, false)
    fun setOngoingNotification(value : Boolean) {
        prefs.edit().putBoolean(IS_ONGOING, value).apply()
    }

    fun getAppThemeType() : Int {
        val themeType = prefs.getInt(APP_THEME, THEME_STANDARD)
        return if (themeType < 0 || themeType > THEME_BLUE_LIGHT) THEME_STANDARD else themeType
    }
    fun setAppThemeType(value : Int) {
        prefs.edit().putInt(APP_THEME, value).apply()
    }

    private val _notificationsRepeatTimeFlow: MutableStateFlow<NotificationRepeatTime> by lazy {
        MutableStateFlow(getNotificationsRepeatTime())
    }
    val notificationsRepeatTimeFlow: StateFlow<NotificationRepeatTime> by lazy {
        _notificationsRepeatTimeFlow.asStateFlow()
    }
    fun getNotificationsRepeatTime() : NotificationRepeatTime {
        val value = prefs.getInt(NOTIFICATIONS_REPEAT_TIME, MINUTES_60.ordinal)
        return NotificationRepeatTime.valueOf(value)
    }
    fun setNotificationsRepeatTime(value : Int) {
        prefs.edit().putInt(NOTIFICATIONS_REPEAT_TIME, value).apply()
        _notificationsRepeatTimeFlow.value = NotificationRepeatTime.valueOf(value)
    }
}
