package relaxeddd.englishnotify.ui.main

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Insets
import android.media.AudioManager
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.view.View
import android.view.WindowInsets
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import dagger.android.support.DaggerAppCompatActivity
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.common_ui_func.doOnApplyWindowInsets
import relaxeddd.englishnotify.common_ui_func.openWebApplication
import relaxeddd.englishnotify.databinding.MainActivityBinding
import relaxeddd.englishnotify.databinding.NavigationHeaderBinding
import relaxeddd.englishnotify.dialogs.DialogPatchNotes
import relaxeddd.englishnotify.dialogs.DialogRateApp
import relaxeddd.englishnotify.dialogs.DialogVoiceInput
import relaxeddd.englishnotify.domain_words.entity.Word
import relaxeddd.englishnotify.preferences.Preferences
import relaxeddd.englishnotify.preferences.utils.THEME_BLACK
import relaxeddd.englishnotify.preferences.utils.THEME_BLUE
import relaxeddd.englishnotify.preferences.utils.THEME_BLUE_LIGHT
import relaxeddd.englishnotify.preferences.utils.THEME_STANDARD
import relaxeddd.englishnotify.view_base.interfaces.IFabOwner
import relaxeddd.englishnotify.view_base.interfaces.INavControllerOwner
import relaxeddd.englishnotify.view_base.interfaces.INavigationOwner
import relaxeddd.englishnotify.view_base.interfaces.IToolbarOwner
import relaxeddd.englishnotify.view_base.interfaces.ListenerResult
import java.util.*
import javax.inject.Inject
import kotlin.system.exitProcess

@MainActivityBeforeRefactoringWarning
class MainActivity : DaggerAppCompatActivity(), INavigationOwner, IToolbarOwner, IFabOwner, INavControllerOwner {

    companion object {
        const val REQUEST_RECOGNIZE_SPEECH = 5242
        private const val REQUEST_NOTIFICATIONS_PERMISSION_CODE = 4234

        private const val NAV_ID_NONE = -1

        private val TOP_LEVEL_DESTINATIONS = setOf(
            R.id.fragmentDictionaryContainer,
            R.id.fragmentTrainingSetting,
            R.id.fragmentNotifications,
            R.id.fragmentSettings
        )
    }

    @Inject
    lateinit var prefs: Preferences

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    val viewModel by viewModels<ViewModelMain> { viewModelFactory }

    private lateinit var binding: MainActivityBinding

    private var currentFragmentId: Int = NAV_ID_NONE
    private lateinit var navController: NavController

    private var tts: TextToSpeech? = null
    private var isTtsInit = false
    private var isTtsInitFailed = false
    private var lastVoiceText: String? = null
    private var isFastSpeechSpeed = false
    private var isLastPlayedEng = true
    private var isPlaying = false
    private var recognizeSpeechCallback: ((String?) -> Unit)? = null

    var isMyResumed = false

    //------------------------------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater).also {
            setContentView(it.root)
            setupTheme(it)
        }

        volumeControlStream = AudioManager.STREAM_MUSIC

        val isBottomNavigation = prefs.isBottomNavigation()
        val initialNavigationId = prefs.getStartFragmentId() ?: R.id.fragmentDictionaryContainer
        initInsets(binding, isBottomNavigation)
        initNavigation(binding, isBottomNavigation, initialNavigationId)

        val hasNotificationsPermission =
            VERSION.SDK_INT < VERSION_CODES.TIRAMISU || checkSelfPermission(POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        viewModel.onViewCreate(hasNotificationsPermission)

        subscribeToViewModel()
    }

    override fun onResume() {
        super.onResume()
        isMyResumed = true
    }

    override fun onPause() {
        super.onPause()
        isMyResumed = false
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
        currentFragmentId = binding.drawerNavigation.checkedItem?.itemId ?: NAV_ID_NONE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_NOTIFICATIONS_PERMISSION_CODE) {
            viewModel.showPatchNotesIfNeeded()
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_RECOGNIZE_SPEECH -> {
                if (resultCode == Activity.RESULT_OK) {
                    val spokenText = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).let { results ->
                        results?.get(0)
                    } ?: ""
                    recognizeSpeechCallback?.invoke(spokenText)
                }
                recognizeSpeechCallback = null
            }
            else -> {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun registerToolbar(toolbar: Toolbar) {
        if (!prefs.isBottomNavigation()) {
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
            NAVIGATION_DIALOG_PATCH_NOTES -> {
                val dialog = DialogPatchNotes()
                dialog.show(supportFragmentManager, "Patch Notes Dialog")
            }
            NAVIGATION_REQUEST_NOTIFICATIONS_PERMISSION -> {
                if (VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
                    requestPermissions(arrayOf(POST_NOTIFICATIONS), REQUEST_NOTIFICATIONS_PERMISSION_CODE)
                }
            }
            NAVIGATION_RECREATE_ACTIVITY -> {
                if (isMyResumed) {
                    finish()
                    startActivity(Intent(this, MainActivity::class.java).apply { addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION) })
                }
            }
            NAVIGATION_LOADING_SHOW -> setLoadingVisible(true)
            NAVIGATION_LOADING_HIDE -> setLoadingVisible(false)
        }
    }

    override fun getNavController() = Navigation.findNavController(this, R.id.fragment_navigation_host)

    //------------------------------------------------------------------------------------------------------------------
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
                    showToast(this, getString(R.string.error_word_voice, it.toString()))
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

    //------------------------------------------------------------------------------------------------------------------
    private fun subscribeToViewModel() {
        viewModel.navigation.observe(this) {
            it.getContentIfNotHandled()?.let { eventId ->
                onNavigationEvent(eventId)
            }
        }
        viewModel.isShowLoading.observe(this) {
            binding.containerMainProgressBar.isVisible = it
        }
        viewModel.isBottomNavigation.observe(this) {
            binding.bottomNavigationViewMain.isVisible = it
            binding.drawerNavigation.isVisible = !it
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
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, null)
        isPlaying = false
    }

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

    private fun setupTheme(binding: MainActivityBinding) {
        when (prefs.getAppThemeType()) {
            THEME_STANDARD -> setTheme(R.style.AppTheme)
            THEME_BLUE -> setTheme(R.style.AppTheme2)
            THEME_BLACK -> setTheme(R.style.AppTheme3)
            THEME_BLUE_LIGHT -> setTheme(R.style.AppTheme4)
            else -> setTheme(R.style.AppTheme)
        }

        val appThemeType = prefs.getAppThemeType()
        val isNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        binding.bottomNavigationViewMain.setBackgroundColor(ContextCompat.getColor(this, if (isNightMode) R.color.bottom_navigation_color else getPrimaryColorResId(appThemeType)))
        binding.bottomNavigationViewMain.itemBackgroundResource = if (isNightMode) R.color.bottom_navigation_color else getPrimaryColorResId(appThemeType)
        binding.buttonMainFab.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, if (isNightMode) R.color.floating_button_color else getPrimaryColorResId(appThemeType)))
    }

    private fun initInsets(binding: MainActivityBinding, isBottomNavigation: Boolean) {
        if (!isBottomNavigation) {
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
    }

    private fun initNavigation(binding: MainActivityBinding, isBottomNavigation: Boolean, initialNavigationId: Int) {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_navigation_host) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopLevelTab = isTopLevelTab(destination.id)

            currentFragmentId = destination.id
            if (isTopLevelTab) {
                prefs.setStartFragmentId(destination.id)
            }
            if (!isBottomNavigation) {
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

        binding.bottomNavigationViewMain.setOnNavigationItemSelectedListener {
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

        if (!isBottomNavigation) {
            binding.drawerNavigation.let { drawerNavigation ->
                val menuView = findViewById<RecyclerView>(R.id.design_navigation_view)

                NavigationHeaderBinding.inflate(layoutInflater).let { navigationHeaderView ->
                    drawerNavigation.doOnApplyWindowInsets { v, insets, padding ->
                        v.updatePadding(top = padding.top + insets.systemWindowInsetTop)
                        menuView?.updatePadding(bottom = insets.systemWindowInsetBottom)
                    }
                    drawerNavigation.addHeaderView(navigationHeaderView.root)
                    navigationHeaderView.imageNavigationHeaderLike.setOnClickListener {
                        openWebApplication(this@MainActivity)
                    }
                    navigationHeaderView.imageHeaderLogo.setOnClickListener {
                        onNavigationEvent(NAVIGATION_DIALOG_RATE_APP)
                    }
                }
                drawerNavigation.itemBackground = navigationItemBackground(this)

                setupWithNavController(drawerNavigation, navController)
                drawerNavigation.setCheckedItem(initialNavigationId)
            }
        } else {
            binding.bottomNavigationViewMain.selectedItemId = initialNavigationId
        }

        when (initialNavigationId) {
            R.id.fragmentTrainingSetting -> navController.myNavigate(R.id.action_global_fragmentTrainingSetting)
            R.id.fragmentNotifications -> navController.myNavigate(R.id.action_global_fragmentNotifications)
            R.id.fragmentSettings -> navController.myNavigate(R.id.action_global_fragmentSettings)
        }
    }

    private fun isTopLevelTab(tabResId: Int) = TOP_LEVEL_DESTINATIONS.contains(tabResId)
}
