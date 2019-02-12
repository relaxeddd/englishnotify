package com.example.vchechin.testapp.model.http

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ListenerRegistration
import java.util.*

object Cache : Observable() {

    var firebaseUser: FirebaseUser? = null
    var user: User? = null
    var tokenId: String? = null
    val userInventory = ArrayList<Purchase>()
    val purchaseResults = ArrayList<PurchaseResult>()
    var alerts: ArrayList<Alert> = ArrayList()
    var appInfo: AppInfo = AppInfo(-1)

    private var registerListenerUser: ListenerRegistration? = null
    private var registerListenerAppInfo: ListenerRegistration? = null
    private var registerListenerAlerts: ListenerRegistration? = null

    override fun notifyObservers(arg: Any?) {
        setChanged()
        super.notifyObservers(arg)
    }

    fun clear() {
        firebaseUser = null
        user = null
        tokenId = null

        userInventory.clear()
        purchaseResults.clear()

        alerts = ArrayList()

        registerListenerUser?.remove()
        registerListenerAlerts?.remove()
    }

    fun removePurchase(purchase: Purchase) {
        val inventoryIt = userInventory.iterator()

        while (inventoryIt.hasNext()) {
            if (inventoryIt.next().token == purchase.token) {
                inventoryIt.remove()
                break
            }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    fun requestVerifyPurchase(purchaseTokenId: String, signature: String, originalJson: String, itemType: String,
                              doOnReceive: (purchaseResult: PurchaseResult) -> Unit, doOnError: (error: Throwable?) -> Unit = {}) {
        if (!isNetworkAvailable()) return

        ApiHelper.getVerifyPurchaseObservable(purchaseTokenId, signature, originalJson, itemType).subscribe ({ purchase ->
            if (purchase.result.code != SERVER_RESULT_OK) showToast(getErrorTextRes(purchase.result.code))
            doOnReceive(purchase)
        }, {
            showToast(getString(R.string.error_purchase_request) + ": " + (it?.message ?: "Unknown error"))
            doOnError(it)
        })
    }

    fun requestSetNickname(nickname: String, doOnReceive: (result: Result) -> Unit, doOnError: (error: Throwable?) -> Unit) {
        if (!isNetworkAvailable()) return

        ApiHelper.getSetNicknameObservable(nickname).subscribe ({ result ->
            if (result.code != SERVER_RESULT_OK) showToast(getErrorTextRes(result.code))
            doOnReceive(result)
        }, {
            showToast(getString(R.string.error_set_nickname) + ": " + (it?.message ?: "Unknown error"))
            doOnError(it)
        })
    }

    fun requestSendFeedback(message: String, contactInfo: String, doOnReceive: (result: Result) -> Unit, doOnError: (error: Throwable?) -> Unit) {
        if (!isNetworkAvailable()) return

        ApiHelper.getSendFeedbackObservable(message, contactInfo).subscribe ({ result ->
            if (result.code != SERVER_RESULT_OK) showToast(getErrorTextRes(result.code))
            doOnReceive(result)
        }, {
            showToast(getString(R.string.error_send_feedback) + ": " + (it?.message ?: "Unknown error"))
            doOnError(it)
        })
    }

    @UseInLoading
    fun requestInit(pushToken: String, doOnReceive: (initData: InitData) -> Unit, doOnError: (error: Throwable?) -> Unit) {
        if (!isNetworkAvailable()) return

        val userId = Cache.firebaseUser?.uid ?: ""

        if (userId.isEmpty()) {
            doOnError(Throwable("Invalid user id"))
            return
        }

        ApiHelper.getInitObservable(pushToken).subscribe ({ initData ->
            if (initData.result != null && initData.result.code == SERVER_RESULT_OK) {
                user = initData.user

                registerListenerUser?.remove()
                registerListenerUser = Firestore.subscribeToUserChange(userId, EventListener(){ docSnapshot, _ ->
                    if (docSnapshot !== null && docSnapshot.exists()) {
                        user = parseUser(docSnapshot.data)
                        notifyObservers(ObservableMsgType.USER)
                    }
                })

                doOnReceive(initData)
            } else {
                doOnError(Throwable("Error init"))
            }
        }, {
            doOnError(it)
        })
    }

    //------------------------------------------------------------------------------------------------------------------
    fun tryRequestUser(doOnReceive: (user: User?) -> Unit, doOnError: (error: Throwable?) -> Unit) {
        if (!Firestore.isBlocked()) {
            requestUser(doOnReceive, doOnError)
        } else {
            Firestore.addUnblockListener { requestUser(doOnReceive, doOnError)}
        }
    }

    fun tryRequestAppInfo(doOnReceive: (appInfo: AppInfo) -> Unit, doOnError: (error: Throwable?) -> Unit) {
        if (!Firestore.isBlocked()) {
            requestAppInfo(doOnReceive, doOnError)
        } else {
            Firestore.addUnblockListener { requestAppInfo(doOnReceive, doOnError) }
        }
    }

    fun tryRequestAlerts(doOnReceive: (alerts: List<Alert>) -> Unit, doOnError: (error: Throwable?) -> Unit) {
        if (!Firestore.isBlocked()) {
            requestAlerts(doOnReceive, doOnError)
        } else {
            Firestore.addUnblockListener { requestAlerts(doOnReceive, doOnError) }
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private fun requestUser(doOnReceive: (user: User?) -> Unit, doOnError: (error: Throwable?) -> Unit) {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable()) return

        Firestore.getUserObservable(userId)?.subscribe({ userData ->
            user = parseUser(userData)

            registerListenerUser?.remove()
            registerListenerUser = Firestore.subscribeToUserChange(userId, EventListener{ docSnapshot, _ ->
                if (docSnapshot !== null && docSnapshot.exists()) {
                    user = parseUser(docSnapshot.data)
                    notifyObservers(ObservableMsgType.USER)
                }
            })
            notifyObservers(ObservableMsgType.USER)
            doOnReceive(user)
        }, {
            doOnError(it)
        })
    }

    private fun requestAppInfo(doOnReceive: (appInfo: AppInfo) -> Unit, doOnError: (error: Throwable?) -> Unit) {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || userId.isEmpty()) return

        Firestore.getAppInfoObservable()?.subscribe ({ answer ->
            appInfo = parseAppInfo(answer)

            registerListenerAppInfo?.remove()
            registerListenerAppInfo = Firestore.subscribeToAppInfo(EventListener{ docSnapshot, _ ->
                val source = if (docSnapshot != null && docSnapshot.metadata.hasPendingWrites()) "Local" else "Server"

                if (docSnapshot !== null && docSnapshot.exists()) {
                    appInfo = parseAppInfo(docSnapshot.data)
                    notifyObservers(ObservableMsgType.APP_INFO)
                }
            })

            doOnReceive(appInfo)
        }, {
            doOnError(it)
        })
    }

    private fun requestAlerts(doOnReceive: (alerts: ArrayList<Alert>) -> Unit, doOnError: (error: Throwable?) -> Unit) {
        val userId = firebaseUser?.uid ?: ""

        if (!isNetworkAvailable() || userId.isEmpty()) return

        Firestore.getAlertsObservable()?.subscribe ({ answer ->
            alerts = parseAlerts(answer)

            doOnReceive(alerts)

            registerListenerAlerts?.remove()
            registerListenerAlerts = Firestore.subscribeToAlertsChange(EventListener{ querySnapshot, _ ->
                if (querySnapshot !== null && !querySnapshot.documents.isEmpty()) {
                    alerts = parseAlerts(querySnapshot.documents)
                }

                notifyObservers(ObservableMsgType.ALERTS)
            })
        }, {
            doOnError(it)
        })
    }
}