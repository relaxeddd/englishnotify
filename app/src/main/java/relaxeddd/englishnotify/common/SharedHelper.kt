package relaxeddd.englishnotify.common

import android.content.Context
import relaxeddd.englishnotify.App

object SharedHelper {

    const val NOTIFICATIONS_VIEW_WITH_TRANSLATE = 0
    const val NOTIFICATIONS_VIEW_WITH_QUESTION = 1

    fun isPrivacyPolicyConfirmed(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(PRIVACY_POLICY_CONFIRMED, false)
    }

    fun setPrivacyPolicyConfirmed(isConfirmed : Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(PRIVACY_POLICY_CONFIRMED, isConfirmed).apply()
    }

    fun getLearnLanguageType(context: Context = App.context) : Int {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getInt(PUSH_LANGUAGE, TYPE_PUSH_ENGLISH)
    }

    fun setLearnLanguageType(type : Int, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putInt(PUSH_LANGUAGE, type).apply()
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
        return sPref.getInt(NOTIFICATIONS_VIEW, 0)
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

    fun isHideLearnStage(context: Context = App.context) : Boolean {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(WORD_LEARN_STAGE, false)
    }

    fun setHideLearnStage(isHide: Boolean, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(WORD_LEARN_STAGE, isHide).apply()
    }

    fun getSelectedCategory(context: Context = App.context) : String {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getString(SELECTED_CATEGORY, "") ?: ""
    }

    fun setSelectedCategory(string : String, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putString(SELECTED_CATEGORY, string).apply()
    }
}