package relaxeddd.englishnotify.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.dialogs.DialogPrivacyPolicy
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.main_activity.*
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.MainActivityBinding
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

    private var selectedBottomMenuId: Int = R.id.fragmentDictionaryMain
    lateinit var navController: NavController
    private val providers: List<AuthUI.IdpConfig> = Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
    private var dialogNewVersion: DialogNewVersion? = null
    private var isBillingInited = false

    private val listenerPrivacyPolicy: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onPrivacyPolicyConfirmedResult(result)
        }
    }

    private val listenerNewVersion: ListenerResult<Boolean> = object: ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            if (result) {
                openWebApplication(this@MainActivity)
            }
            finish()
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

        navigation_view_main.setOnNavigationItemSelectedListener {
            if (it.itemId == selectedBottomMenuId) {
                return@setOnNavigationItemSelectedListener true
            }

            when (it.itemId) {
                R.id.fragmentDictionaryMain -> navController.navigate(R.id.action_global_fragmentDictionaryMain)
                R.id.fragmentNotifications -> navController.navigate(R.id.action_global_fragmentNotifications)
                R.id.fragmentSettings -> navController.navigate(R.id.action_global_fragmentSettings)
                else -> return@setOnNavigationItemSelectedListener false
            }
            selectedBottomMenuId = it.itemId

            return@setOnNavigationItemSelectedListener true
        }
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
                    viewModel.requestInitUser()
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
            NAVIGATION_DIALOG_PRIVACY_POLICY -> {
                val dialog = DialogPrivacyPolicy()
                dialog.setConfirmListener(listenerPrivacyPolicy)
                dialog.show(this@MainActivity.supportFragmentManager, "Privacy Policy Dialog")
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
}
