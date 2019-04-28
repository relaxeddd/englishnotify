package relaxeddd.englishnotify.common

const val URL_FIREBASE = "https://us-central1-push-english-79db7.cloudfunctions.net/"
const val URL_LOCAL = "http://192.168.1.69:5000/push-english-79db7/us-central1/"

const val USER_ID_TEST = "id123456"

const val CHECKED_ITEM = "checked_item"
const val CHECKED_ITEMS = "checked_items"
const val DATABASE_TEST_APP = "database_test_app"
const val ITEMS = "items"
const val SELECTED_ITEM = "selected_item"
const val WORDS = "words"
const val USERS = "users"

const val FUNC_REQUEST_INIT = "requestInitNew/"
const val FUNC_REQUEST_VERIFY_PURCHASE = "requestVerifyPurchase/"
const val FUNC_REQUEST_SEND_FEEDBACK = "requestSendFeedback/"
const val FUNC_REQUEST_UPDATE_USER = "requestUpdateUser/"
const val FUNC_REQUEST_SEND_TEST_NOTIFICATION = "requestSendTestNotification/"

const val EMPTY_RES = -1

const val RESULT_ERROR_UNAUTHORIZED = 403
const val RESULT_ERROR_USER_NOT_FOUND = 771
const val RESULT_ERROR_APP_INIT = 774

const val RESULT_UNDEFINED = 0
const val RESULT_OK = 700
const val RESULT_ERROR_INTERNET = 17
const val RESULT_ERROR_UPDATE_USER = 1101

const val RESULT_PURCHASE_NOT_VERIFIED = 1401
const val RESULT_PURCHASE_ALREADY_RECEIVED = 1402
const val RESULT_PURCHASE_VERIFIED_ERROR = 1403

const val RESULT_ERROR_ADD_PUSH_TOKEN = 1501

const val RESULT_ERROR_SEND_FEEDBACK = 1601
const val RESULT_ERROR_FEEDBACK_TOO_SHORT = 1602
const val RESULT_LOCAL_ERROR = 5101
const val RESULT_ERROR_NETWORK = 5102

const val RESULT_ERROR_TEST_NOTIFICATION = 6100

const val NAVIGATION_EXIT = 999
const val NAVIGATION_DIALOG_REPEAT = 1000
const val NAVIGATION_DIALOG_CHECK_TAGS = 1001
const val NAVIGATION_DIALOG_SORT_BY = 1002
const val NAVIGATION_FRAGMENT_NOTIFICATIONS = 1003
const val NAVIGATION_FRAGMENT_SETTINGS = 1004
const val NAVIGATION_DIALOG_LEARN_ENGLISH = 1005
const val NAVIGATION_DIALOG_APP_ABOUT = 1006
const val NAVIGATION_DIALOG_PRIVACY_POLICY = 1007
const val NAVIGATION_GOOGLE_AUTH = 1008
const val NAVIGATION_DIALOG_SEND_FEEDBACK = 1009
const val NAVIGATION_GOOGLE_LOGOUT = 1010
const val NAVIGATION_DIALOG_CONFIRM_LOGOUT = 1011
const val NAVIGATION_WEB_PLAY_MARKET = 1012
const val NAVIGATION_DIALOG_SUBSCRIPTION = 1013
const val NAVIGATION_DIALOG_RATE_APP = 1020
const val NAVIGATION_DIALOG_NOTIFICATIONS_VIEW = 1030
const val NAVIGATION_DIALOG_NEW_VERSION = 1040
const val NAVIGATION_DIALOG_PATCH_NOTES = 1050
const val NAVIGATION_DIALOG_NIGHT_TIME = 1060
const val NAVIGATION_DIALOG_TEST_NOTIFICATIONS = 1070
const val NAVIGATION_INIT_BILLING = 1080

const val TYPE_PUSH_ENGLISH = 0
const val TYPE_PUSH_RUSSIAN = 1

const val ENG = "eng"
const val RUS = "rus"
const val V2 = "v2"
const val V3 = "v3"
const val TRANSCRIPTION = "transcription"
const val TAGS = "tags"
const val SAMPLES = "samples"
const val SAMPLE_RUS = "sampleRus"
const val SAMPLE_ENG = "sampleEng"

const val ID = "id"
const val EMAIL = "email"
const val RECEIVE_NOTIFICATIONS = "receiveNotifications"
const val NOTIFICATIONS_TIME_TYPE = "notificationsTimeType"
const val TAGS_AVAILABLE = "tagsAvailable"
const val TAGS_SELECTED = "tagsSelected"
const val LEARN_LANGUAGE_TYPE = "learnLanguageType"
const val WORD_ID = "wordId"
const val NOTIFICATION_ID = "notificationId"

const val START_HOUR = "startHour"
const val DURATION_HOURS = "durationHours"

const val START_HOUR_OFF = "startHourOff"
const val DURATION_HOURS_OFF = "durationHoursOff"

const val ERROR_TOKEN_NOT_INIT = "User token not init"
const val ERROR_NOT_AUTHORIZED = "User not authorized"
const val ERROR_USER_NOT_FOUND = "User not found"
const val ERROR_REWARDS_NOT_FOUND = "Rewards not found"
const val ERROR_ACHIEVEMENTS_NOT_FOUND = "Achievements not found"
const val ERROR_APP_INFO = "App info not found"
const val ERROR_SEND_TEST_NOTIFICATION = "Error send test notification"

const val COLLECTION_USERS = "users"
const val COLLECTION_MESSAGES = "messages"
const val COLLECTION_INFO = "info"
const val COLLECTION_PURCHASE = "purchase"
const val COLLECTION_ALERTS = "alerts"
const val COLLECTION_DICTIONARY = "dictionary"

const val APP = "app"
const val IS_NOT_REMIND_RATE = "isNotRemindRate"
const val APP_LOGIN_COUNT = "appLoginCount"
const val TUTORIAL = "tutorial"
const val DIALOG = "Dialog"
const val RATING_LIST = "ratingList"
const val MAX_BET = "maxBet"
const val IS_RECOVERY_AVAILABLE = "isRecoveryAvailable"
const val IS_FREE_SPINS_AVAIlABLE = "isFreeSpinsAvailable"
const val IS_CANCELABLE = "isCancelable"
const val IS_CAN_CHANGE_NAME = "isCanChangeName"
const val IS_KNOW = "isKnow"
const val HONOR = "honor"
const val COINS_RECOVERY = "coinsRecovery"
const val FREE_SPINS_TYPES = "freeSpinsTypes"
const val QUICK_SPIN_GRADE = "quickSpinGrade"
const val CONTENT = "content"
const val CONTINUE_GAME = "continueGame"
const val CURRENT_CONTINUE_GAME = "currentContinueGame"
const val FREE_SPINS_BET = "freeSpinsBet"
const val NAME = "name"
const val VALUE = "value"
const val CURRENCY = "currency"
const val RATING_POSITION = "ratingPosition"
const val BANK = "bank"
const val TIMESTAMP = "timestamp"
const val MULTIPLIER = "multiplier"
const val USER_NAME = "userName"
const val MAIN = "main"
const val CURRENT_FREE_SPINS = "currentFreeSpins"
const val OPTIONS = "options"
const val STICKY_WILDS = "stickyWilds"
const val SYMBOL_WILD = "symbolWild"
const val TYPE = "type"
const val DAY = "day"
const val CURRENT_DAY = "currentDay"
const val LIST = "list"
const val REWARDS = "rewards"
const val TITLE = "title"
const val TEXT = "text"
const val GRADE = "grade"
const val BET = "bet"
const val WIN = "win"
const val CANCELLED_RATE_DIALOG = "cancelleRateDialog"
const val SYSTEM = "system"
const val LOGIN_DATA = "loginData"
const val SELECTED_TAGS = "selectedTags"
const val PRIVACY_POLICY_CONFIRMED = "privacyPolicyConfirmed"
const val PUSH_LANGUAGE = "pushLanguage"
const val PUSH_TOKEN = "pushToken"
const val LAUNCH_COUNT = "launchCount"
const val NOTIFICATIONS_VIEW = "notificationsView"
const val SORT_BY_TYPE = "sortByType"
const val COUNT = "count"
const val GOAL = "goal"
const val MESSAGE = "message"
const val MESSAGE_RES_NAME = "message_res_name"
const val IS_RECEIVED = "isReceived"
const val RECEIVED = "received"
const val RECEIVED_ACHIEVEMENTS = "receivedAchievements"
const val ACHIEVEMENTS = "achievements"
const val LEVELS = "levels"
const val X = "x"
const val Y = "y"
const val RES_NAME_TITLE = "resNameTitle"
const val RES_NAME_MESSAGE = "resNameMessage"
const val RES_NAME_ICON = "resNameIcon"
const val RES_NAME_BUTTON_CONFIRM = "resNameButtonConfirm"
const val RES_NAME_BUTTON_CANCEL = "resNameButtonCancel"