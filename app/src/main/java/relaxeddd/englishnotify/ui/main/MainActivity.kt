package relaxeddd.englishnotify.ui.main

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Insets
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.MainActivityBinding
import relaxeddd.englishnotify.databinding.NavigationHeaderBinding
import relaxeddd.englishnotify.dialogs.*
import relaxeddd.englishnotify.donate.ActivityBilling
import relaxeddd.englishnotify.model.preferences.SharedHelper
import relaxeddd.englishnotify.push.PushTokenHelper
import java.util.*
import kotlin.system.exitProcess

class MainActivity : ActivityBilling<ViewModelMain, MainActivityBinding>(), NavigationHost, FloatingActionButtonHost {

    companion object {
        const val REQUEST_SIGN_IN = 1312
        const val REQUEST_PLAY_SERVICES_RESULT = 7245
        const val REQUEST_RECOGNIZE_SPEECH = 5242

        private const val NAV_ID_NONE = -1

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.fragmentDictionaryContainer,
            R.id.fragmentTrainingSetting,
            R.id.fragmentNotifications,
            R.id.fragmentSettings
        )
    }

    private var currentFragmentId: Int = NAV_ID_NONE
    private lateinit var navController: NavController
    private val providers: List<AuthUI.IdpConfig> = listOf(AuthUI.IdpConfig.GoogleBuilder().build())
    private var navigationHeaderBinding: NavigationHeaderBinding? = null

    private var tts: TextToSpeech? = null
    private var isTtsInit = false
    private var isTtsInitFailed = false
    private var lastVoiceText: String? = null
    private var isFastSpeechSpeed = false
    private var isLastPlayedEng = true
    private var isPlaying = false
    private var recognizeSpeechCallback: ((String?) -> Unit)? = null

    private val listenerNewVersion: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            if (result) {
                openWebApplication(this@MainActivity)
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

        volumeControlStream = AudioManager.STREAM_MUSIC
        PushTokenHelper.initChannelNotifications(this)
        initGooglePlayServices()

        val isOldDesign = SharedHelper.isOldNavigationDesign()

        navigationHeaderBinding = NavigationHeaderBinding.inflate(layoutInflater).apply {
            lifecycleOwner = this@MainActivity
        }

        if (!isOldDesign) {
            binding.drawerContainer.setOnApplyWindowInsetsListener { v, insets ->
                v.onApplyWindowInsets(insets)
                v.updatePadding(
                    left = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        insets.getInsets(WindowInsets.Type.systemBars()).left
                    } else {
                        @Suppress("DEPRECATION")
                        insets.systemWindowInsetLeft
                    },
                    right = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        insets.getInsets(WindowInsets.Type.systemBars()).right
                    } else {
                        @Suppress("DEPRECATION")
                        insets.systemWindowInsetRight
                    }
                )

                val topInset = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    insets.getInsets(WindowInsets.Type.systemBars()).top
                } else {
                    @Suppress("DEPRECATION")
                    insets.systemWindowInsetTop
                }
                val bottomInset = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    insets.getInsets(WindowInsets.Type.systemBars()).bottom
                } else {
                    @Suppress("DEPRECATION")
                    insets.systemWindowInsetBottom
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowInsets.Builder(insets).setInsets(WindowInsets.Type.systemBars(), Insets.of(0, topInset, 0, bottomInset)).build()
                } else {
                    @Suppress("DEPRECATION")
                    insets.replaceSystemWindowInsets(0, topInset, 0, bottomInset)
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            binding.containerMainActivity.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        binding.containerMainActivity.setOnApplyWindowInsetsListener(NoopWindowInsetsListener)
        binding.statusBarScrim.setOnApplyWindowInsetsListener(HeightTopWindowInsetsListener)
        binding.containerMainSignIn.doOnApplyWindowInsets { v, insets, padding ->
            if (!isOldDesign) {
                (v.layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin = padding.bottom + insets.systemWindowInsetBottom
            }
        }

        val initialNavigationId = SharedHelper.getStartFragmentId()
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_navigation_host) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopLevelTab = isTopLevelTab(destination.id)

            currentFragmentId = destination.id
            if (isTopLevelTab) {
                SharedHelper.setStartFragmentId(destination.id)
            }
            if (!isOldDesign) {
                val lockMode = if (isTopLevelTab) {
                    DrawerLayout.LOCK_MODE_UNLOCKED
                } else {
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED
                }
                binding.drawer.setDrawerLockMode(lockMode)
            } else {
                binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        Navigation.setViewNavController(binding.buttonMainFab, navController)

        binding.navigationViewMain.setOnNavigationItemSelectedListener {
            if (it.itemId == currentFragmentId) {
                return@setOnNavigationItemSelectedListener true
            }
            when (it.itemId) {
                R.id.fragmentDictionaryContainer -> navController.myNavigate(R.id.action_global_fragmentDictionaryContainer)
                R.id.fragmentTrainingSetting -> navController.myNavigate(R.id.action_global_fragmentTrainingSetting)
                R.id.fragmentNotifications -> navController.myNavigate(R.id.action_global_fragmentNotifications)
                R.id.fragmentSettings -> navController.myNavigate(R.id.action_global_fragmentSettings)
                else -> return@setOnNavigationItemSelectedListener false
            }
            currentFragmentId = it.itemId

            return@setOnNavigationItemSelectedListener true
        }

        if (!isOldDesign) {
            binding.navigation.apply {
                val menuView = findViewById<RecyclerView>(R.id.design_navigation_view)

                navigationHeaderBinding?.apply {
                    doOnApplyWindowInsets { v, insets, padding ->
                        v.updatePadding(top = padding.top + insets.systemWindowInsetTop)
                        menuView?.updatePadding(bottom = insets.systemWindowInsetBottom)
                    }
                    addHeaderView(root)
                    imageNavigationHeaderLike.setOnClickListener {
                        openWebApplication(this@MainActivity)
                    }
                }
                itemBackground = navigationItemBackground(context)

                setupWithNavController(this, navController)
            }
        }

        findViewById<ImageView>(R.id.image_header_logo)?.setOnClickListener {
            onNavigationEvent(NAVIGATION_DIALOG_RATE_APP)
        }

        initPrivacyPolicyText(binding.textMainPrivacyPolicy, this)

        viewModel.onViewCreate()

        if (!isOldDesign) {
            binding.navigation.setCheckedItem(initialNavigationId)
        } else {
            binding.navigationViewMain.selectedItemId = initialNavigationId
        }
        when (initialNavigationId) {
            R.id.fragmentTrainingSetting -> navController.myNavigate(R.id.action_global_fragmentTrainingSetting)
            R.id.fragmentNotifications -> navController.myNavigate(R.id.action_global_fragmentNotifications)
            R.id.fragmentSettings -> navController.myNavigate(R.id.action_global_fragmentSettings)
        }
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentFragmentId = binding.navigation.checkedItem?.itemId ?: NAV_ID_NONE
    }

    override fun registerToolbar(toolbar: Toolbar) {
        if (!SharedHelper.isOldNavigationDesign()) {
            val appBarConfiguration = AppBarConfiguration.Builder(TOP_LEVEL_DESTINATIONS).setOpenableLayout(binding.drawer).build()
            toolbar.setupWithNavController(navController, appBarConfiguration)
        }
    }

    override fun configureFab(iconResId: Int?, listener: (View.OnClickListener)?) {
        if (iconResId != null && iconResId != EMPTY_RES) {
            binding.buttonMainFab.visibility = View.VISIBLE
            binding.buttonMainFab.setImageResource(iconResId)
        } else {
            binding.buttonMainFab.visibility = View.GONE
        }
        binding.buttonMainFab.setOnClickListener(listener)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_SIGN_IN -> {
                val response: IdpResponse? = IdpResponse.fromResultIntent(data)

                if (resultCode == Activity.RESULT_OK) {
                    binding.textMainPrivacyPolicy.visibility = View.GONE
                    viewModel.requestInit()
                } else if (isMyResumed && response != null) {
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {}
                    showToast(response.error.toString())
                }
            }
            REQUEST_RECOGNIZE_SPEECH -> {
                if (resultCode == Activity.RESULT_OK) {
                    val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                        results?.get(0)
                    } ?: ""
                    recognizeSpeechCallback?.invoke(spokenText)
                }
                recognizeSpeechCallback = null
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
                    navController.myNavigate(R.id.action_global_fragmentNotifications)
                }
            }
            NAVIGATION_FRAGMENT_SETTINGS -> {
                if (navController.currentDestination?.label != getString(R.string.label_fragment_settings)) {
                    navController.myNavigate(R.id.action_global_fragmentSettings)
                }
            }
            NAVIGATION_DIALOG_RATE_APP -> {
                if (isMyResumed) {
                    val dialog = DialogRateApp()
                    dialog.confirmListener = object : ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) {
                                openWebApplication(this@MainActivity)
                            }
                        }
                    }
                    dialog.show(supportFragmentManager, "Rate app Dialog")
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
                val dialogNewVersion = DialogNewVersion()
                dialogNewVersion.confirmListener = listenerNewVersion
                dialogNewVersion.show(supportFragmentManager, "New version Dialog")
            }
            NAVIGATION_DIALOG_PATCH_NOTES -> {
                val dialog = DialogPatchNotes()
                dialog.show(supportFragmentManager, "Patch Notes Dialog")
            }
            NAVIGATION_GOOGLE_LOGOUT -> {
                if (isMyResumed) {
                    viewModel.isShowLoading.value = true
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {
                        viewModel.isShowLoading.value = false
                    }
                }
            }
            NAVIGATION_RECREATE_ACTIVITY -> {
                if (isMyResumed) {
                    finish()
                    startActivity(Intent(this, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) })
                }
            }
            NAVIGATION_DIALOG_SUBSCRIPTION_INFO -> {
                if (isMyResumed) {
                    val dialog = DialogSubscriptionInfo()
                    dialog.confirmListener = object : ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) {
                                onNavigationEvent(NAVIGATION_DIALOG_SUBSCRIPTION)
                            }
                        }
                    }
                    dialog.show(supportFragmentManager, "Sub Info Dialog")
                }
            }
            NAVIGATION_DIALOG_SUBSCRIPTION_REQUIRED -> {
                if (isMyResumed) {
                    val dialog = DialogNeedSubscription()
                    dialog.confirmListener = object : ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) {
                                onNavigationEvent(NAVIGATION_DIALOG_SUBSCRIPTION_INFO)
                            }
                        }
                    }
                    dialog.show(supportFragmentManager, "Sub Info Dialog")
                }
            }
            NAVIGATION_LOADING_SHOW -> setLoadingVisible(true)
            NAVIGATION_LOADING_HIDE -> setLoadingVisible(false)
            else -> super.onNavigationEvent(eventId)
        }
    }

    override fun setupThemeColors() {
        super.setupThemeColors()
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        binding.navigationViewMain.setBackgroundColor(ContextCompat.getColor(this, if (isNightMode) R.color.bottom_navigation_color else getPrimaryColorResId()))
        binding.navigationViewMain.itemBackgroundResource = if (isNightMode) R.color.bottom_navigation_color else getPrimaryColorResId()
        binding.buttonMainFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, if (isNightMode) R.color.floating_button_color else getPrimaryColorResId()))
    }

    fun setLoadingVisible(isVisible: Boolean) {
        viewModel.isShowLoading.value = isVisible
    }

    fun playWord(word: Word?) {
        playText(word?.eng)
    }

    fun playText(text: String?) {
        if (isPlaying) return

        text ?: return
        val textToSpeak = text.replace("_", "").replace("|", "")
        if (textToSpeak.isEmpty()) return

        val isEngPlay = text.any { (it in 'a'..'z') || (it in 'A'..'Z') }

        isPlaying = true
        if (tts == null || !isTtsInit || isTtsInitFailed || (isLastPlayedEng != isEngPlay)) {
            isLastPlayedEng = isEngPlay
            isTtsInitFailed = false

            tts = TextToSpeech(this) {
                if (it == TextToSpeech.SUCCESS) {
                    tts?.language = if (isLastPlayedEng) Locale.US else Locale.getDefault()
                    isTtsInit = true
                    speak(textToSpeak)
                    lastVoiceText = textToSpeak
                } else {
                    isPlaying = false
                    isTtsInitFailed = true
                    showToast(getString(R.string.error_word_voice, it.toString()))
                }
            }
        } else {
            speak(textToSpeak)
            lastVoiceText = textToSpeak
        }
    }

    fun requestRecognizeSpeech(localeString: String, recognizeSpeechCallback: ((String?) -> Unit)) {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, getLocaleStringByKey(localeString) ?: Locale.getDefault())
            }
            this.recognizeSpeechCallback = recognizeSpeechCallback
            startActivityForResult(intent, REQUEST_RECOGNIZE_SPEECH)
        } catch (e: Exception) {
            val dialog = DialogVoiceInput()
            dialog.confirmListener = object: ListenerResult<Boolean> {
                override fun onResult(result: Boolean) {
                    if (result) {
                        recognizeSpeechCallback(null)
                    }
                    this@MainActivity.recognizeSpeechCallback = null
                }
            }
            dialog.show(supportFragmentManager, "Restore word Dialog")
        }
    }

    private fun speak(textToSpeak: String) {
        if (textToSpeak == lastVoiceText && isFastSpeechSpeed) {
            tts?.setSpeechRate(0.5f)
            isFastSpeechSpeed = false
        } else {
            tts?.setSpeechRate(1f)
            isFastSpeechSpeed = true
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
            isPlaying = false
        } else {
            @Suppress("DEPRECATION")
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null)
            isPlaying = false
        }
    }

    private fun initGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                val dialog = googleApiAvailability.getErrorDialog(this, status, REQUEST_PLAY_SERVICES_RESULT)
                dialog?.setOnCancelListener { finish() }
                dialog?.show()
            }
        } else {
            GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
        }
    }

    private fun isTopLevelTab(tabResId: Int) = TOP_LEVEL_DESTINATIONS.contains(tabResId)

    private fun getLocaleStringByKey(key: String) = when(key) {
        "EN" -> Locale.US.toString()
        "RU" -> Locale("ru", "RU").toString()
        "DE" -> Locale.GERMANY.toString()
        "ES" -> Locale("es", "ES").toString()
        "FR" -> Locale.FRANCE.toString()
        "ZH" -> Locale.CHINA.toString()
        "JA" -> Locale.JAPAN.toString()
        "IT" -> Locale.ITALY.toString()
        else -> null
    }
}
