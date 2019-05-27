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

    fun getLearnStage0(context: Context = App.context) : MutableSet<String> {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getStringSet(LEARN_STAGE_0, HashSet<String>()) ?: HashSet()
    }

    fun setLearnStage0(value : MutableSet<String>, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putStringSet(LEARN_STAGE_0, value).apply()
    }

    fun getLearnStage1(context: Context = App.context) : MutableSet<String> {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getStringSet(LEARN_STAGE_1, HashSet<String>()) ?: HashSet()
    }

    fun setLearnStage1(value : MutableSet<String>, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putStringSet(LEARN_STAGE_1, value).apply()
    }

    fun getLearnStage2(context: Context = App.context) : MutableSet<String> {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getStringSet(LEARN_STAGE_2, HashSet<String>()) ?: HashSet()
    }

    fun setLearnStage2(value : MutableSet<String>, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putStringSet(LEARN_STAGE_2, value).apply()
    }

    fun getLearnStage3(context: Context = App.context) : MutableSet<String> {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getStringSet(LEARN_STAGE_3, HashSet<String>()) ?: HashSet()
    }

    fun setLearnStage3(value : MutableSet<String>, context: Context = App.context) {
        val sPref = context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putStringSet(LEARN_STAGE_3, value).apply()
    }
}