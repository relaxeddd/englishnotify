package com.example.vchechin.testapp.common.auth

import com.example.vchechin.testapp.dialogs.DialogPrivacyPolicy

abstract class ActivityAuth<V : AuthView, P : AuthPresenter<V>> : MvpActivityEnglish<V, P>(), AuthView {

    override fun exit() {
        finish()
    }

    override fun showDialogPrivacyPolicy(confirmListener: ListenerConfirm?) {
        val dialogPrivacyPolicy = DialogPrivacyPolicy()
        dialogPrivacyPolicy.setConfirmListener(confirmListener)
        showDialog(dialogPrivacyPolicy, null, false)
    }
}