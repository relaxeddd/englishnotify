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
const val FUNC_REQUEST_INSERT_OWN_WORD = "requestInsertOwnWord/"
const val FUNC_REQUEST_DELETE_OWN_WORDS = "requestDeleteOwnWords/"
const val FUNC_REQUEST_GET_OWN_WORDS = "requestOwnWords/"

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

const val RESULT_ERROR_OWN_WORD = 6301
const val RESULT_ERROR_OWN_WORD_EXISTS = 6302
const val RESULT_ERROR_OWN_WORD_LIMIT = 6303
const val RESULT_ERROR_OWN_WORD_TYPE = 6307
const val RESULT_ERROR_OWN_DELETE_NO_IDS = 6308
const val RESULT_ERROR_OWN_DELETE_NO_WORDS = 6309
const val RESULT_ERROR_OWN_DELETE = 6310
const val RESULT_ERROR_OWN_GET = 6311

const val RESULT_ERROR_NO_SUBSCRIPTION = 6401

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
const val NAVIGATION_DIALOG_OWN_CATEGORY = 1091
const val NAVIGATION_FRAGMENT_TRAINING = 1111
const val NAVIGATION_DIALOG_INFO_TRAINING = 1121

const val NAVIGATION_LOADING_SHOW = 801
const val NAVIGATION_LOADING_HIDE = 802
const val NAVIGATION_ACTIVITY_BACK = 790
const val NAVIGATION_GLOBAL_DICTIONARY = 780
const val NAVIGATION_ACTIVITY_BACK_TWICE = 770

const val TYPE_PUSH_ENGLISH = 0
const val TYPE_PUSH_RUSSIAN = 1

const val TRAINING_ENG_TO_RUS = 0
const val TRAINING_RUS_TO_ENG = 1

const val LEARN_STAGE_MAX = 3

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

const val EXERCISE = "exercise"

const val START_HOUR = "startHour"
const val DURATION_HOURS = "durationHours"

const val START_HOUR_OFF = "startHourOff"
const val DURATION_HOURS_OFF = "durationHoursOff"

const val ERROR_TOKEN_NOT_INIT = "User token not init"
const val ERROR_NOT_AUTHORIZED = "User not authorized"
const val ERROR_USER_NOT_FOUND = "User not found"
const val ERROR_APP_INFO = "App info not found"
const val ERROR_SEND_TEST_NOTIFICATION = "Error send test notification"

const val APP = "app"
const val IS_KNOW = "isKnow"
const val CATEGORY = "category"
const val CONTENT = "content"
const val NAME = "name"
const val VALUE = "value"
const val TIMESTAMP = "timestamp"
const val TYPE = "type"
const val TITLE = "title"
const val TEXT = "text"
const val CANCELLED_RATE_DIALOG = "cancelledRateDialog2"
const val WORD_LEARN_STAGE = "wordLearnStage"
const val IS_SHOW_ONLY_ONE_NOTIFICATION = "isShowOnlyOneNotification"
const val SYSTEM = "system"
const val OWN_WORD = "ownWord"
const val PUSH = "push"
const val LOGIN_DATA = "loginData"
const val SELECTED_TAGS = "selectedTags"
const val PRIVACY_POLICY_CONFIRMED = "privacyPolicyConfirmed"
const val PUSH_LANGUAGE = "pushLanguage"
const val PUSH_TOKEN = "pushToken"
const val LAUNCH_COUNT = "launchCount"
const val NOTIFICATIONS_VIEW = "notificationsView"
const val SELECTED_CATEGORY = "selectedCategory"
const val LOCAL_SELECTED_CATEGORY = "localSelectedCategory"
const val SORT_BY_TYPE = "sortByType"
const val TRAINING_CATEGORY = "trainingCategory"
const val TRAINING_LANGUAGE = "trainingLanguage"
const val COUNT = "count"
const val TRAINING_TYPE = "trainingType"

const val ENGLISH_WORDS_NOTIFICATIONS_CHANNEL = "EnglishWordsNotificationsChannel"

const val OWN = "own"
const val IRREGULAR = "irregular"
const val HARD = "hard"
const val ALL_APP_WORDS = "all_app_words"
const val ALL_APP_WORDS_WITHOUT_SIMPLE = "all_app_words_without_simple"
const val HARD_5 = "hard_5"
const val PRONOUN = "pronoun"
const val PROVERB = "proverb"
const val TOURISTS = "tourists"
const val TOURISTS_5 = "tourists_5"

const val HUMAN_BODY = "human_body"
const val HUMAN_BODY_5 = "human_body_5"
const val COLORS = "colors"
const val COLORS_5 = "colors_5"
const val TIME = "time"
const val TIME_5 = "time_5"
const val PHRASES = "phrases"
const val PHRASES_5 = "phrases_5"
const val ANIMALS = "animals"
const val ANIMALS_5 = "animals_5"
const val FAMILY = "family"
const val FAMILY_5 = "family_5"
const val HUMAN_QUALITIES = "human_qualities"
const val HUMAN_QUALITIES_5 = "human_qualities_5"
const val FEELINGS = "feelings"
const val FEELINGS_5 = "feelings_5"
const val EMOTIONS = "emotions"
const val EMOTIONS_5 = "emotions_5"
const val WORK = "work"
const val WORK_5 = "work_5"
const val MOVEMENT = "movement"
const val MOVEMENT_5 = "movement_5"
const val PROFESSIONS = "professions"
const val PROFESSIONS_5 = "professions_5"
const val FREQUENT = "frequent"
const val FREQUENT_5 = "frequent_5"
const val EDUCATION = "education"
const val EDUCATION_5 = "education_5"
const val FOOD = "food"
const val FOOD_5 = "food_5"
const val WEATHER = "weather"
const val WEATHER_5 = "weather_5"
const val HOUSE = "house"
const val HOUSE_5 = "house_5"
const val GEOGRAPHY = "geography"
const val GEOGRAPHY_5 = "geography_5"
const val ENTERTAINMENT = "entertainment"
const val ENTERTAINMENT_5 = "entertainment_5"
const val SPORT = "sport"
const val SPORT_5 = "sport_5"
const val AUTO = "auto"
const val AUTO_5 = "auto_5"
const val FREQUENT_VERBS = "frequent_verbs"
const val FREQUENT_VERBS_5 = "frequent_verbs_5"
const val EXERCISES_VERBS_FIRST = "exercises_verbs_first"