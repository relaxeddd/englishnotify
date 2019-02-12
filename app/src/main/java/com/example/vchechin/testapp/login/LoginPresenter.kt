package com.example.vchechin.testapp.login

import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class LoginPresenter : AuthPresenter<LoginView>() {

    private val providers: List<AuthUI.IdpConfig> = Arrays.asList(AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build())
    private var enteredNickname: String? = null

    override fun onNoAuth() {
        ifViewAttached { it.initLayout() }
    }

    override fun onLoginSuccess(initData: InitData) {
        MainPresenter.initData = initData
        loginSuccess()
    }

    override fun onLoginError() {
        showToast(R.string.error_authorisation)
        ifViewAttached {
            it.initLayout()
            it.setButtonsEnabled(true)
        }
    }

    fun loginGoogle() {
        if (!isNetworkAvailable()) return

        if (!isAlreadyLoggedIn()) {
            ifViewAttached { it.loginGoogle(providers) }
        } else {
            initUser()
        }
    }

    fun loginGoogleSuccess() {
        initUser()
    }

    fun loginGoogleError() {
        showToast(R.string.login_error)
    }

    //------------------------------------------------------------------------------------------------------------------
    private fun isAlreadyLoggedIn() : Boolean {
        Cache.firebaseUser = FirebaseAuth.getInstance().currentUser
        return Cache.firebaseUser != null
    }

    private fun loginSuccess() {
        ifViewAttached {
            it.startActivityMain()
        }
    }

    private fun waitUserCreation() {
        val timer = Timer()
        val onUserCreateListener = {
            val nickname = enteredNickname

            if (nickname != null) {
                Cache.requestSetNickname(nickname, {
                    ifViewAttached { it.hideLoading() }
                    loginSuccess()
                }, {
                    ifViewAttached { it.hideLoading() }
                    loginSuccess()
                })
            }
        }

        ifViewAttached { it.showLoading(getString(R.string.creating_account)) }
        timer.schedule(TimerTaskWaitUserCreation(onUserCreateListener), 5000, 5000)
    }

    //------------------------------------------------------------------------------------------------------------------
    private inner class TimerTaskWaitUserCreation(val onUserCreateListener: (() -> Unit)) : TimerTask() {

        override fun run() {
            Cache.tryRequestUser({
                onUserCreateListener()
                cancel()
            }, { })
        }
    }
}