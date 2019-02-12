package com.example.vchechin.testapp.login

import android.content.Intent
import android.os.Bundle
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse

class ActivityLogin : ActivityAuth<LoginView, LoginPresenter>(), LoginView {

    private var isInit = false

    companion object {
        const val REQUEST_SIGN_IN = 1312
    }

    //------------------------------------------------------------------------------------------------------------------
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
        presenter.onViewCreate()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            REQUEST_SIGN_IN -> {
                val response: IdpResponse? = IdpResponse.fromResultIntent(data)

                if (resultCode == MvpActivity.RESULT_OK) {
                    presenter.loginGoogleSuccess()
                } else {
                    showToast(response.toString())
                    //presenter.loginGoogleError()
                }
            }
        }
    }

    override fun createPresenter(): LoginPresenter = LoginPresenter()

    //------------------------------------------------------------------------------------------------------------------
    override fun initLayout() {
        if (isInit) return

        setContentView(R.layout.activity_login)

        button_sign_in_google.setOnClickListener {
            presenter.loginGoogle()
        }

        isInit = true
    }

    override fun startActivityMain() {
        finish()
        launchActivity<ActivityMain>()
    }

    override fun loginGoogle(providers: List<AuthUI.IdpConfig>) {
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            REQUEST_SIGN_IN)
    }

    override fun setButtonsEnabled(isEnabled: Boolean) {
        button_sign_in_google.isEnabled = isEnabled
    }
}