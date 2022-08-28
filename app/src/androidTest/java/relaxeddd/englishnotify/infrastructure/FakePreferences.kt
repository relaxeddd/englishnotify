package relaxeddd.englishnotify.infrastructure

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.models.NotificationRepeatTime
import javax.inject.Inject

class FakePreferences @Inject constructor() : Preferences {

    override fun isNotificationsEnabled(): Boolean {
        return false
    }

    override fun setNotificationsEnabled(value: Boolean) {

    }

    override fun isPrivacyPolicyConfirmed(): Boolean {
        return true
    }

    override fun setPrivacyPolicyConfirmed(isConfirmed: Boolean) {

    }

    override fun isDefaultWordsLoaded(): Boolean {
        return true
    }

    override fun setDefaultWordsLoaded(value: Boolean) {

    }

    var isBottomNavigationValue = true

    override fun isBottomNavigation(): Boolean {
        return isBottomNavigationValue
    }

    override fun setBottomNavigation(value: Boolean) {

    }

    override fun isCheckLearnedWords(): Boolean {
        return false
    }

    override fun setCheckLearnedWords(value: Boolean) {

    }

    override fun isListeningTraining(): Boolean {
        return false
    }

    override fun setListeningTraining(value: Boolean) {

    }

    override fun isEnabledSecondaryProgress(): Boolean {
        return false
    }

    override fun setEnabledSecondaryProgress(value: Boolean) {

    }

    override fun isHearAnswer(): Boolean {
        return false
    }

    override fun setHearAnswer(value: Boolean) {

    }

    override fun isShowProgressInTraining(): Boolean {
        return false
    }

    override fun setShowProgressInTraining(value: Boolean) {

    }

    override fun isShowVoiceInput(): Boolean {
        return false
    }

    override fun setShowVoiceInput(value: Boolean) {

    }

    override fun getSelectedLocaleWord(): Int {
        return 0
    }

    override fun setSelectedLocaleWord(value: Int) {

    }

    override fun getTrueAnswersToLearn(): Int {
        return 3
    }

    override fun setTrueAnswersToLearn(value: Int) {

    }

    override fun getNotificationLearnPoints(): Int {
        return 1
    }

    override fun setNotificationLearnPoints(value: Int) {

    }

    override fun getSelectedLocaleTranslation(): Int {
        return 0
    }

    override fun setSelectedLocaleTranslation(value: Int) {

    }

    override fun getSelectedLocaleTraining(): Int {
        return 0
    }

    override fun setSelectedLocaleTraining(value: Int) {

    }

    override fun getStartFragmentId(): Int? {
        return 0
    }

    override fun setStartFragmentId(value: Int) {

    }

    override val learnLanguageTypeFlow: StateFlow<Int>
        get() = MutableStateFlow(0)

    override fun getLearnLanguageType(): Int {
        return 0
    }

    override fun setLearnLanguageType(type: Int) {

    }

    override fun getDictionaryTabPosition(): Int {
        return 0
    }

    override fun setDictionaryTabPosition(type: Int) {

    }

    override fun getLastOwnCategory(): String {
        return ""
    }

    override fun setLastOwnCategory(pushToken: String) {

    }

    override fun getSortByType(): String {
        return ""
    }

    override fun setSortByType(sortType: String) {

    }

    override fun getNotificationsView(): Int? {
        return 0
    }

    override fun setNotificationsView(viewType: Int) {

    }

    override fun isPatchNotesViewed(version: String): Boolean {
        return true
    }

    override fun setPatchNotesViewed(version: String) {

    }

    override fun getStartHour(): Int {
        return 0
    }

    override fun setStartHour(count: Int) {

    }

    override fun getDurationHours(): Int {
        return 0
    }

    override fun setDurationHours(count: Int) {

    }

    override val selectedCategoryFlow: StateFlow<String>
        get() = MutableStateFlow("")

    override fun getSelectedCategory(): String {
        return ""
    }

    override fun setSelectedCategory(string: String) {

    }

    override fun getTrainingCategory(): String {
        return ""
    }

    override fun setTrainingCategory(string: String) {

    }

    override fun getTrainingLanguage(): Int {
        return 0
    }

    override fun setTrainingLanguage(value: Int) {

    }

    override fun isShowOnlyOneNotification(): Boolean {
        return false
    }

    override fun setShowOnlyOneNotification(value: Boolean) {

    }

    override fun isOngoingNotification(): Boolean {
        return false
    }

    override fun setOngoingNotification(value: Boolean) {

    }

    override fun getAppThemeType(): Int {
        return 0
    }

    override fun setAppThemeType(value: Int) {

    }

    override val notificationsRepeatTimeFlow: StateFlow<NotificationRepeatTime>
        get() = MutableStateFlow(NotificationRepeatTime.MINUTES_30)

    override fun getNotificationsRepeatTime(): NotificationRepeatTime {
        return NotificationRepeatTime.MINUTES_30
    }

    override fun setNotificationsRepeatTime(value: Int) {

    }
}
