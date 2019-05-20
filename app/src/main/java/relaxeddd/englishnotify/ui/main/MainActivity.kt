package relaxeddd.englishnotify.ui.main

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.UnderlineSpan
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import relaxeddd.englishnotify.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.main_activity.*
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.MainActivityBinding
import relaxeddd.englishnotify.dialogs.DialogChangeAccount
import relaxeddd.englishnotify.dialogs.DialogNewVersion
import relaxeddd.englishnotify.dialogs.DialogPatchNotes
import relaxeddd.englishnotify.dialogs.DialogRateApp
import relaxeddd.englishnotify.donate.ActivityBilling
import relaxeddd.englishnotify.push.PushTokenHelper
import java.util.*

class MainActivity : ActivityBilling<ViewModelMain, MainActivityBinding>() {

    companion object {
        const val REQUEST_SIGN_IN = 1312
        const val REQUEST_PLAY_SERVICES_RESULT = 7245
    }

    private var selectedBottomMenuId: Int = R.id.fragmentDictionaryAll
    private var selectedSecondaryBottomMenuId: Int = R.id.fragmentDictionaryAll
    private lateinit var navController: NavController
    private val providers: List<AuthUI.IdpConfig> = Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
    private var dialogNewVersion: DialogNewVersion? = null
    var isBillingInited = false

    private val listenerNewVersion: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            if (result) {
                openWebApplication(this@MainActivity)
            }
        }
    }

    private val listenerChangeAccount: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onDialogChangeAccountResult(result)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            REQUEST_SIGN_IN -> {
                val response: IdpResponse? = IdpResponse.fromResultIntent(data)

                if (response?.errorCode == -1) {
                    viewModel.prepareInit()
                } else {
                    AuthUI.getInstance().signOut(this).addOnCompleteListener {}
                    showToast(response.toString())
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
            NAVIGATION_FRAGMENT_NOTIFICATIONS -> {
                if (navController.currentDestination?.label != getString(R.string.label_fragment_notifications)) {
                    navController.navigate(R.id.fragmentNotifications)
                }
            }
            NAVIGATION_FRAGMENT_SETTINGS -> {
                if (navController.currentDestination?.label != getString(R.string.label_fragment_settings)) {
                    navController.navigate(R.id.fragmentSettings)
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
                System.exit(0)
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
            NAVIGATION_DIALOG_CHANGE_ACCOUNT -> {
                val dialog = DialogChangeAccount()
                dialog.confirmListener = listenerChangeAccount
                dialog.show(this@MainActivity.supportFragmentManager, "Change account Dialog")
            }
            NAVIGATION_DIALOG_PATCH_NOTES -> {
                val dialog = DialogPatchNotes()
                dialog.show(this@MainActivity.supportFragmentManager, "Patch Notes Dialog")
            }
            NAVIGATION_INIT_BILLING -> {
                if (isMyResumed && !isBillingInited) {
                    initBilling(object: ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) isBillingInited = true
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
            else -> super.onNavigationEvent(eventId)
        }
    }

    fun setLoadingVisible(isVisible: Boolean) {
        viewModel.isShowLoading.value = isVisible
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
