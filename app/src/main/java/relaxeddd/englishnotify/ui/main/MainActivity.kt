package relaxeddd.englishnotify.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.dialogs.DialogPrivacyPolicy
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.databinding.MainActivityBinding
import relaxeddd.englishnotify.push.PushTokenHelper.initChannelNotifications
import java.util.*

class MainActivity : ActivityBase<ViewModelMain, MainActivityBinding>() {

    companion object {
        const val REQUEST_SIGN_IN = 1312
    }

    private val uiScope = CoroutineScope(Dispatchers.Main)
    lateinit var navController: NavController
    private val providers: List<AuthUI.IdpConfig> = Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())

    private val listenerPrivacyPolicy: ListenerResult<Boolean> = object:
        ListenerResult<Boolean> {
        override fun onResult(result: Boolean) {
            viewModel.onPrivacyPolicyConfirmedResult(result)
        }
    }

    override fun getLayoutResId() = R.layout.main_activity
    override fun getViewModelFactory() = InjectorUtils.provideMainViewModelFactory(this)
    override fun getViewModelClass(): Class<ViewModelMain> = ViewModelMain::class.java

    override fun configureBinding() {
        super.configureBinding()
        binding.viewModel = viewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initChannelNotifications(this)
        navController = Navigation.findNavController(this, R.id.fragment_navigation_host)

        NavigationUI.setupWithNavController(navigation_view_main, navController)
        viewModel.onViewCreate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onViewResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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
                dialog.show(this@MainActivity.supportFragmentManager, "Learn Language Dialog")
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
            else -> super.onNavigationEvent(eventId)
        }
    }
}
