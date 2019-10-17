package relaxeddd.englishnotify.ui.main

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import relaxeddd.englishnotify.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.main_activity.*
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.MainActivityBinding
import relaxeddd.englishnotify.dialogs.*
import relaxeddd.englishnotify.donate.ActivityBilling
import relaxeddd.englishnotify.push.PushTokenHelper
import java.util.*
import kotlin.system.exitProcess

class MainActivity : ActivityBilling<ViewModelMain, MainActivityBinding>() {

    companion object {
        const val REQUEST_SIGN_IN = 1312
        const val REQUEST_PLAY_SERVICES_RESULT = 7245
    }

    private var selectedBottomMenuId: Int = R.id.fragmentDictionaryAll
    private var selectedSecondaryBottomMenuId: Int = R.id.fragmentDictionaryAll
    private lateinit var navController: NavController
    private val providers: List<AuthUI.IdpConfig> = listOf(AuthUI.IdpConfig.GoogleBuilder().build())
    private var dialogNewVersion: DialogNewVersion? = null
    var isBillingInit = false

    private var tts: TextToSpeech? = null
    private var isTtsInit = false
    private var isTtsInitFailed = false
    private var lastVoiceWordId: String? = null
    private var isFastSpeechSpeed = true

    private val listenerNewVersion: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            if (result) {
                openWebApplication(this@MainActivity)
            }
        }
    }

    private val listenerFeedbackDialog: ListenerResult<String> = object: ListenerResult<String> {
        override fun onResult(result: String) {
            viewModel.onFeedbackDialogResult(result)
        }
    }

    private val listenerLikeApp: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            if (result) {
                onNavigationEvent(NAVIGATION_DIALOG_RATE_APP)
            } else {
                onNavigationEvent(NAVIGATION_DIALOG_SEND_FEEDBACK)
            }
        }
    }

    override fun getLayoutResId() = R.layout.main_activity
    override fun getViewModelFactory() = InjectorUtils.provideMainViewModelFactory()
    override fun getViewModelClass(): Class<ViewModelMain> = ViewModelMain::class.java

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PushTokenHelper.initChannelNotifications(this)
        initGooglePlayServices()

        navController = Navigation.findNavController(this, R.id.fragment_navigation_host)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isDictionaryTab = isDictionaryTab(destination.id)
            viewModel.isVisibleSecondaryBottomNavigationView.value = isDictionaryTab

            if (isDictionaryTab) {
                if (destination.id != selectedSecondaryBottomMenuId) {
                    selectedSecondaryBottomMenuId = destination.id
                    navigation_view_main_secondary.selectedItemId = destination.id
                }
                if (selectedBottomMenuId != R.id.fragmentDictionaryAll) {
                    selectedBottomMenuId = R.id.fragmentDictionaryAll
                    navigation_view_main.selectedItemId = R.id.fragmentDictionaryAll
                }
            } else if (destination.id != selectedBottomMenuId) {
                selectedBottomMenuId = destination.id
                navigation_view_main.selectedItemId = destination.id
            }
        }

        navigation_view_main.setOnNavigationItemSelectedListener {
            if (it.itemId == selectedBottomMenuId) {
                return@setOnNavigationItemSelectedListener true
            }

            when (it.itemId) {
                R.id.fragmentDictionaryAll -> {
                    when (selectedSecondaryBottomMenuId) {
                        R.id.fragmentDictionaryAll -> navController.navigate(R.id.action_global_fragmentDictionaryAll)
                        R.id.fragmentDictionaryOwn -> navController.navigate(R.id.action_global_fragmentDictionaryOwn)
                        R.id.fragmentDictionaryExercises -> navController.navigate(R.id.action_global_fragmentDictionaryExercises)
                        R.id.fragmentDictionaryKnow -> navController.navigate(R.id.action_global_fragmentDictionaryKnow)
                        else -> return@setOnNavigationItemSelectedListener false
                    }
                }
                R.id.fragmentTrainingSetting -> navController.navigate(R.id.action_global_fragmentTrainingSetting)
                R.id.fragmentNotifications -> navController.navigate(R.id.action_global_fragmentNotifications)
                R.id.fragmentSettings -> navController.navigate(R.id.action_global_fragmentSettings)
                else -> return@setOnNavigationItemSelectedListener false
            }
            selectedBottomMenuId = it.itemId

            return@setOnNavigationItemSelectedListener true
        }
        navigation_view_main_secondary.setOnNavigationItemSelectedListener {
            if (it.itemId == selectedSecondaryBottomMenuId) {
                return@setOnNavigationItemSelectedListener true
            }

            when (it.itemId) {
                R.id.fragmentDictionaryAll -> navController.navigate(R.id.action_global_fragmentDictionaryAll)
                R.id.fragmentDictionaryOwn -> navController.navigate(R.id.action_global_fragmentDictionaryOwn)
                R.id.fragmentDictionaryExercises -> navController.navigate(R.id.action_global_fragmentDictionaryExercises)
                R.id.fragmentDictionaryKnow -> navController.navigate(R.id.action_global_fragmentDictionaryKnow)
                else -> return@setOnNavigationItemSelectedListener false
            }
            selectedSecondaryBottomMenuId = it.itemId

            return@setOnNavigationItemSelectedListener true
        }
        initPrivacyPolicyText()

        viewModel.onViewCreate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onViewResume()
    }

    override fun onStop() {
        super.onStop()
        tts?.shutdown()
        tts = null
        isTtsInit = false
        isTtsInitFailed = false
    }

    override fun onBackPressed() {
        if (viewModel.isShowLoading.value == true) {
            viewModel.isShowLoading.value = false
        } else {
            super.onBackPressed()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_SIGN_IN -> {
                val response: IdpResponse? = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    text_main_privacy_policy.visibility = View.GONE
                    viewModel.requestInit()
                } else if (isMyResumed && response != null) {
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {}
                    showToast(response.error.toString())
                }
            }
            REQUEST_PLAY_SERVICES_RESULT -> {
                finish()
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_ACTIVITY_BACK -> {
                onBackPressed()
            }
            NAVIGATION_FRAGMENT_NOTIFICATIONS -> {
                if (navController.currentDestination?.label != getString(R.string.label_fragment_notifications)) {
                    navController.navigate(R.id.action_global_fragmentNotifications)
                }
            }
            NAVIGATION_FRAGMENT_SETTINGS -> {
                if (navController.currentDestination?.label != getString(R.string.label_fragment_settings)) {
                    navController.navigate(R.id.action_global_fragmentSettings)
                }
            }
            NAVIGATION_DIALOG_RATE_APP -> {
                if (isMyResumed) {
                    val dialog = DialogRateApp()
                    dialog.show(this@MainActivity.supportFragmentManager, "Rate app Dialog")
                }
            }
            NAVIGATION_EXIT -> {
                finishAffinity()
                exitProcess(0)
            }
            NAVIGATION_GOOGLE_AUTH -> {
                startActivityForResult(
                    AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                    REQUEST_SIGN_IN
                )
            }
            NAVIGATION_DIALOG_NEW_VERSION -> {
                if (dialogNewVersion == null) {
                    dialogNewVersion = DialogNewVersion()
                    dialogNewVersion?.confirmListener = listenerNewVersion
                    dialogNewVersion?.show(this@MainActivity.supportFragmentManager, "New version Dialog")
                }
            }
            NAVIGATION_DIALOG_PATCH_NOTES -> {
                val dialog = DialogPatchNotes()
                dialog.show(this@MainActivity.supportFragmentManager, "Patch Notes Dialog")
            }
            NAVIGATION_INIT_BILLING -> {
                if (isMyResumed && !isBillingInit) {
                    initBilling(object: ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) isBillingInit = true
                        }
                    })
                }
            }
            NAVIGATION_GOOGLE_LOGOUT -> {
                if (isMyResumed) {
                    viewModel.isShowLoading.value = true
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {
                        viewModel.isShowLoading.value = false
                    }
                }
            }
            NAVIGATION_DIALOG_SEND_FEEDBACK -> {
                val dialog = DialogSendFeedback()
                dialog.setConfirmListener(listenerFeedbackDialog)
                dialog.show(this.supportFragmentManager, "Send feedback Dialog")
            }
            NAVIGATION_LOADING_SHOW -> setLoadingVisible(true)
            NAVIGATION_LOADING_HIDE -> setLoadingVisible(false)
            else -> super.onNavigationEvent(eventId)
        }
    }

    override fun setupThemeColors() {
        super.setupThemeColors()
        navigation_view_main.setBackgroundColor(getPrimaryColorResId())
        navigation_view_main.itemBackgroundResource = getPrimaryColorResId()
        navigation_view_main_secondary.setBackgroundColor(getPrimaryColorResId())
        navigation_view_main_secondary.itemBackgroundResource = getPrimaryColorResId()
        view_main_bottom_separator.setBackgroundColor(getPrimaryDarkColorResId())
    }

    fun setLoadingVisible(isVisible: Boolean) {
        viewModel.isShowLoading.value = isVisible
    }

    fun playWord(word: Word?) {
        if (tts == null || isTtsInitFailed) {
            isTtsInitFailed = false

            tts = TextToSpeech(this, TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS) {
                    tts?.language = Locale.US
                    isTtsInit = true
                    speak(word)
                } else {
                    isTtsInitFailed = true
                    showToast(getString(R.string.error_word_voice, it.toString()))
                }
            })
        } else if (isTtsInit && word != null && word.eng.isNotEmpty()) {
            speak(word)
        }
    }

    private fun speak(word: Word?) {
        if (word == null || word.eng.isEmpty()) return
        val textSpeech = word.eng.replace("_", "").replace("|", "")

        if (word.id == lastVoiceWordId && isFastSpeechSpeed) {
            tts?.setSpeechRate(0.5f)
            isFastSpeechSpeed = false
        } else if (!isFastSpeechSpeed) {
            tts?.setSpeechRate(1f)
            isFastSpeechSpeed = true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts?.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null, null)
        } else {
            @Suppress("DEPRECATION")
            tts?.speak(textSpeech, TextToSpeech.QUEUE_FLUSH, null)
        }
        lastVoiceWordId = word.id
    }

    private fun initGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                val dialog = googleApiAvailability.getErrorDialog(this, status, REQUEST_PLAY_SERVICES_RESULT)
                dialog.setOnCancelListener { finish() }
                dialog.show()
            }
        } else {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        }
    }

    private fun isDictionaryTab(tabResId: Int) = tabResId == R.id.fragmentDictionaryAll
            || tabResId == R.id.fragmentDictionaryOwn || tabResId == R.id.fragmentDictionaryExercises
            || tabResId == R.id.fragmentDictionaryKnow

    private fun initPrivacyPolicyText() {
        if (SharedHelper.isPrivacyPolicyConfirmed(this)) {
            text_main_privacy_policy.visibility = View.GONE
            return
        }

        val privacyPolicy = text_main_privacy_policy.text.toString()
        val spannablePrivacyPolicy = SpannableString(privacyPolicy)
        val clickablePrivacyPolicy = object : ClickableSpan() {
            override fun onClick(textView: View) {
                openWebPrivacyPolicy(this@MainActivity)
            }
        }

        setClickableSubstring(privacyPolicy, spannablePrivacyPolicy, getString(R.string.privacy_policy_in_sentence), clickablePrivacyPolicy)

        text_main_privacy_policy.text = spannablePrivacyPolicy
        text_main_privacy_policy.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setClickableSubstring(string: String, spannableString: SpannableString, substring: String, clickableSpan: ClickableSpan) {
        val firstIndex = string.indexOf(substring)
        val lastIndex = firstIndex + substring.length

        spannableString.setSpan(clickableSpan, firstIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), firstIndex, lastIndex, 0)
    }
}
