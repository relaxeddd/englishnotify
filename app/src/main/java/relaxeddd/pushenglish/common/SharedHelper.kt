package relaxeddd.pushenglish.common

import android.content.Context
import relaxeddd.pushenglish.App

object SharedHelper {

    fun isPrivacyPolicyConfirmed() : Boolean {
        val sPref = App.context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return sPref.getBoolean(PRIVACY_POLICY_CONFIRMED, false)
    }

    fun setPrivacyPolicyConfirmed(isConfirmed : Boolean) {
        val sPref = App.context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putBoolean(PRIVACY_POLICY_CONFIRMED, isConfirmed).apply()
    }

    fun getSelectedTags() : Set<String> {
        val sPref = App.context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        return HashSet(sPref.getStringSet(SELECTED_TAGS, HashSet<String>()))
    }

    fun setSelectedTags(selectedTags : List<String>) {
        val sPref = App.context.getSharedPreferences(LOGIN_DATA, Context.MODE_PRIVATE)
        sPref.edit().putStringSet(SELECTED_TAGS, HashSet(selectedTags)).apply()
    }
}