package com.example.vchechin.testapp.common.auth

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId

abstract class AuthPresenter<V : AuthView> : MvpEnglishPresenter<V>() {

    abstract fun onNoAuth()
    abstract fun onLoginSuccess(initData: InitData)
    abstract fun onLoginError()

    fun onViewCreate() {
        checkPrivacyPolicyDialog()
    }

    protected fun initUser() {
        Cache.firebaseUser = FirebaseAuth.getInstance().currentUser
        val pushToken = FirebaseInstanceId.getInstance().token ?: ""

        ifViewAttached { it.showLoading(getString(R.string.authorization)) }
        Cache.requestInit(pushToken, { initData ->
            ifViewAttached { it.hideLoading() }

            if (initData.result.code == SERVER_RESULT_OK) {
                onLoginSuccess(initData)
            } else {
                onLoginError()
            }
        }, {
            ifViewAttached { it.hideLoading() }
            onLoginError()
        })
    }

    protected fun checkPrivacyPolicyDialog() {
        val sPref = App.context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        val isConfirmed = sPref.getBoolean(PRIVACY_POLICY_CONFIRMED, false)

        if (isConfirmed) {
            onSuccessCheckPrivacyPolicy()
        } else {
            ifViewAttached {
                it.initLayout()
                it.showDialogPrivacyPolicy(object : ListenerConfirm {
                    override fun onConfirm() {
                        sPref.edit().putBoolean(PRIVACY_POLICY_CONFIRMED, true).apply()
                        onSuccessCheckPrivacyPolicy()
                    }
                    override fun onDecline() {
                        ifViewAttached { it.exit() }
                    }
                })
            }
        }
    }

    private fun onSuccessCheckPrivacyPolicy() {
        if (!isNetworkAvailable()) return

        if (isAlreadyLoggedIn()) {
            initUser()
        } else {
            onNoAuth()
        }
    }

    private fun isAlreadyLoggedIn() : Boolean {
        Cache.firebaseUser = FirebaseAuth.getInstance().currentUser
        return Cache.firebaseUser != null
    }
}