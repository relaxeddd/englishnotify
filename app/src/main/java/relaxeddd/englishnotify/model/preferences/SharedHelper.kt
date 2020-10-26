package relaxeddd.englishnotify.model.preferences

import android.content.Context
import android.os.Build
import relaxeddd.englishnotify.App
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*

object SharedHelper {

    const val NOTIFICATIONS_VIEW_WITH_TRANSLATE = 0
    const val NOTIFICATIONS_VIEW_WITH_QUESTION = 1

    fun isPrivacyPolicyConfirmed(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(PRIVACY_POLICY_CONFIRMED, false)
    }

    fun setPrivacyPolicyConfirmed(isConfirmed: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(PRIVACY_POLICY_CONFIRMED, isConfirmed).apply()
    }

    fun isOldNavigationDesign(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(IS_OLD_NAVIGATION_DESIGN, false)
    }

    fun setOldNavigationDesign(value: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(IS_OLD_NAVIGATION_DESIGN, value).apply()
    }

    fun isCheckLearnedWords(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(CHECK_LEARNED_WORDS, false)
    }

    fun setCheckLearnedWords(value: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(CHECK_LEARNED_WORDS, value).apply()
    }

    fun isListeningTraining(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(LISTENING_TRAINING, false)
    }

    fun setListeningTraining(value: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(LISTENING_TRAINING, value).apply()
    }

    fun isEnabledSecondaryProgress(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(SECONDARY_PROGRESS_ENABLED, false)
    }

    fun setEnabledSecondaryProgress(value: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(SECONDARY_PROGRESS_ENABLED, value).apply()
    }

    fun isHideOffNotificationsWarning(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(HIDE_OFF_NOTIFICATIONS_WARNING, false)
    }

    fun setHideOffNotificationsWarning(value: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(HIDE_OFF_NOTIFICATIONS_WARNING, value).apply()
    }

    fun isHearAnswer(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(HEAR_ANSWER, false)
    }

    fun setHearAnswer(value: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(HEAR_ANSWER, value).apply()
    }

    fun isShowVoiceInput(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(VOICE_INPUT, true)
    }

    fun setShowVoiceInput(value: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(VOICE_INPUT, value).apply()
    }

    fun getSelectedLocaleWord(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(SELECTED_LOCALE_WORD, 0)
    }

    fun setSelectedLocaleWord(value: Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(SELECTED_LOCALE_WORD, value).apply()
    }

    fun getSelectedLocaleTranslation(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(SELECTED_LOCALE_TRANSLATION, 1)
    }

    fun setSelectedLocaleTranslation(value: Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(SELECTED_LOCALE_TRANSLATION, value).apply()
    }

    fun getSelectedLocaleTraining(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(SELECTED_LOCALE_TRAINING, 0)
    }

    fun setSelectedLocaleTraining(value: Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(SELECTED_LOCALE_TRAINING, value).apply()
    }

    fun getStartFragmentId(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(START_FRAGMENT_ID, R.id.fragmentDictionaryContainer)
    }

    fun setStartFragmentId(value : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(START_FRAGMENT_ID, value).apply()
    }

    fun getLearnLanguageType(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(PUSH_LANGUAGE, TYPE_PUSH_ENGLISH)
    }

    fun setLearnLanguageType(type : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(PUSH_LANGUAGE, type).apply()
    }

    fun getDictionaryTabPosition(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(DICTIONARY_TAB_POSITION, 0)
    }

    fun setDictionaryTabPosition(type : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(DICTIONARY_TAB_POSITION, type).apply()
    }

    fun getPushToken(context: Context = App.context) : String {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getString(PUSH_TOKEN, "") ?: ""
    }

    fun setPushToken(pushToken : String, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putString(PUSH_TOKEN, pushToken).apply()
    }

    fun getSortByType(context: Context = App.context) : String {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getString(SORT_BY_TYPE, SortByType.TIME_NEW.name) ?: ""
    }

    fun setSortByType(sortType : String, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putString(SORT_BY_TYPE, sortType).apply()
    }

    fun isCancelledRateDialog(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(CANCELLED_RATE_DIALOG, false)
    }

    fun setCancelledRateDialog(isCancelled: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(CANCELLED_RATE_DIALOG, isCancelled).apply()
    }

    fun getLaunchCount(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(LAUNCH_COUNT, 0)
    }

    fun setLaunchCount(count : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(LAUNCH_COUNT, count).apply()
    }

    fun getNotificationsView(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(NOTIFICATIONS_VIEW, if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) NOTIFICATIONS_VIEW_STANDARD else NOTIFICATIONS_VIEW_INPUT)
    }

    fun setNotificationsView(viewType : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(NOTIFICATIONS_VIEW, viewType).apply()
    }

    fun isPatchNotesViewed(version: String, context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(version, false)
    }

    fun setPatchNotesViewed(version: String, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(version, true).apply()
    }

    fun getStartHour(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(START_HOUR_OFF, 0)
    }

    fun setStartHour(count : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(START_HOUR_OFF, count).apply()
    }

    fun getDurationHours(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(DURATION_HOURS_OFF, 0)
    }

    fun setDurationHours(count : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(DURATION_HOURS_OFF, count).apply()
    }

    fun getUserEmail(context: Context = App.context) : String {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getString(USER_EMAIL, "") ?: ""
    }

    fun setUserEmail(string : String, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putString(USER_EMAIL, string).apply()
    }

    fun getTrainingCategory(context: Context = App.context) : String {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getString(TRAINING_CATEGORY, ALL_APP_WORDS) ?: ""
    }

    fun setTrainingCategory(string : String, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putString(TRAINING_CATEGORY, string).apply()
    }

    fun getTrainingLanguage(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(TRAINING_LANGUAGE, 0)
    }

    fun setTrainingLanguage(value : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(TRAINING_LANGUAGE, value).apply()
    }

    fun isShowOnlyOneNotification(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(IS_SHOW_ONLY_ONE_NOTIFICATION, false)
    }

    fun setShowOnlyOneNotification(value: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(IS_SHOW_ONLY_ONE_NOTIFICATION, value).apply()
    }

    fun isShowOwnWords(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(IS_SHOW_OWN_WORDS, true)
    }

    fun setShowOwnWords(isConfirmed : Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(IS_SHOW_OWN_WORDS, isConfirmed).apply()
    }

    fun isOngoing(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(IS_ONGOING, false)
    }

    fun setOngoing(value : Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(IS_ONGOING, value).apply()
    }

    fun getAppThemeType(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        val themeType = sPref.getInt(APP_THEME, THEME_STANDARD)
        return if (themeType < 0 || themeType > THEME_BLUE_LIGHT) THEME_STANDARD else themeType
    }

    fun setAppThemeType(value : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(APP_THEME, value).apply()
    }
}
