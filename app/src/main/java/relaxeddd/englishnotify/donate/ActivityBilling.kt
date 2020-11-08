package relaxeddd.englishnotify.donate

import androidx.databinding.ViewDataBinding
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.dialogs.DialogSubscription
import relaxeddd.englishnotify.model.http.ApiHelper
import relaxeddd.englishnotify.model.repository.RepositoryCommon
import relaxeddd.englishnotify.model.repository.RepositoryUser
import relaxeddd.englishnotify.ui.main.MainActivity

abstract class ActivityBilling<VM : ViewModelBase, B : ViewDataBinding> : ActivityBase<VM, B>(),
    PurchasesUpdatedListener {

    companion object {
        @BillingClient.SkuType private const val SUB_1 = "sub_1"
        @BillingClient.SkuType private const val SUB_2 = "sub_2"
        @BillingClient.SkuType private const val SUB_3 = "sub_3"

        var listSkuDetails: List<SkuDetails> = ArrayList()
    }

    private var billingClient: BillingClient? = null
    private var isBillingServiceConnected = false
    private var attemptConnect = 0
    private var isBillingInit = false

    private val listenerSubscription: ListenerResult<Int> = object: ListenerResult<Int> {
        override fun onResult(result: Int) {
            onChooseSub(result)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: MutableList<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    requestVerify(purchase)
                }
            }
        } else if (billingResult.responseCode != BillingClient.BillingResponseCode.USER_CANCELED) {
            showToast(R.string.error_purchase)
        }
    }

    override fun onNavigationEvent(eventId: Int) {
        when (eventId) {
            NAVIGATION_INIT_BILLING -> {
                if (isMyResumed && !isBillingInit) {
                    initBilling(object: ListenerResult<Boolean> {
                        override fun onResult(result: Boolean) {
                            if (result) isBillingInit = true
                        }
                    })
                }
            }
            NAVIGATION_DIALOG_SUBSCRIPTION -> {
                if (isMyResumed) {
                    if (isBillingInit) {
                        val dialog = DialogSubscription()
                        dialog.listener = listenerSubscription
                        dialog.show(this.supportFragmentManager, "Subscription Dialog")
                    } else {
                        initBilling(object: ListenerResult<Boolean> {
                            override fun onResult(result: Boolean) {
                                if (isMyResumed) {
                                    if (result) {
                                        isBillingInit = true

                                        val dialog = DialogSubscription()
                                        dialog.listener = listenerSubscription
                                        dialog.show(this@ActivityBilling.supportFragmentManager, "Subscription Dialog")
                                    } else {
                                        showToast(R.string.error_purchase)
                                    }
                                }
                            }
                        })
                    }
                }
            }
            else -> super.onNavigationEvent(eventId)
        }
    }

    private fun requestSkuDetails(resultListener: ListenerResult<Boolean>?) {
        val purchasesInfo = billingClient?.queryPurchases(BillingClient.SkuType.INAPP)

        if (purchasesInfo?.purchasesList?.isNotEmpty() == true) {
            purchasesInfo.purchasesList?.forEach { purchase ->
                if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                    requestVerify(purchase)
                }
            }
        }
        if (listSkuDetails.isNotEmpty()) {
            resultListener?.onResult(true)
            return
        }

        val skuList = ArrayList<String>()
        val params = SkuDetailsParams.newBuilder()

        skuList.add(SUB_1)
        skuList.add(SUB_2)
        skuList.add(SUB_3)
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                listSkuDetails = skuDetailsList ?: emptyList()
            }
            resultListener?.onResult(billingResult.responseCode == BillingClient.BillingResponseCode.OK)
        }
    }

    private fun initBilling(resultListener: ListenerResult<Boolean>? = null) {
        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build()
        }
        if (isBillingServiceConnected) {
            requestSkuDetails(resultListener)
            return
        }

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    isBillingServiceConnected = true
                    requestSkuDetails(resultListener)
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.BILLING_UNAVAILABLE) {
                    showToast("Billing unavailable")
                    resultListener?.onResult(false)
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.DEVELOPER_ERROR) {
                    if (attemptConnect <= 3) {
                        showToast(R.string.loading)
                        attemptConnect++
                    } else {
                        resultListener?.onResult(false)
                    }
                } else {
                    resultListener?.onResult(false)
                }
            }
            override fun onBillingServiceDisconnected() {
                isBillingServiceConnected = false
                resultListener?.onResult(false)
            }
        })
    }

    private fun onChooseSub(subType: Int) {
        val productId = getProductId(subType) ?: return
        var buySkuDetails: SkuDetails? = null

        for (skuDetails in listSkuDetails) {
            if (skuDetails.sku == productId) {
                buySkuDetails = skuDetails
                break
            }
        }

        if (buySkuDetails != null) {
            val flowParams = BillingFlowParams.newBuilder()
                .setSkuDetails(buySkuDetails)
                .build()
            billingClient?.launchBillingFlow(this, flowParams)
        } else {
            showToast(R.string.error_purchase)
        }
    }

    private fun requestVerify(purchase: Purchase?) {
        if (purchase == null || !isNetworkAvailable()) return

        CoroutineScope(Dispatchers.Main).launch {
            val firebaseUser = RepositoryCommon.getInstance().firebaseUser
            val tokenId = RepositoryCommon.getInstance().tokenId

            if (this@ActivityBilling is MainActivity) {
                setLoadingVisible(true)
            }

            val purchaseResult = ApiHelper.requestVerifyPurchase(firebaseUser, tokenId, purchase.purchaseToken,
                purchase.signature, purchase.originalJson, purchase.sku)

            if (this@ActivityBilling is MainActivity) {
                setLoadingVisible(false)
            }
            if (purchaseResult?.result?.isSuccess() == true) {
                onPurchaseResultSuccess(purchaseResult)
            } else if (purchaseResult?.result?.code == RESULT_PURCHASE_ALREADY_RECEIVED) {
                consumePurchase(purchaseResult)
            }
        }
    }

    private fun consumePurchase(purchaseResult: PurchaseResult) {
        val consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchaseResult.tokenId).build()

        billingClient?.consumeAsync(consumeParams) { billingResult, _ ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                showToast(R.string.purchase_consumed)
            }
        }
    }

    private fun onPurchaseResultSuccess(purchaseResult: PurchaseResult) {
        consumePurchase(purchaseResult)

        val user: User? = RepositoryUser.getInstance().liveDataUser.value
        val newSubTime = purchaseResult.refillInfo.subscriptionTime
        val newTestCount = purchaseResult.refillInfo.testCount

        if (newSubTime != 0L && user != null) {
            user.subscriptionTime = newSubTime
            if (newTestCount != 0) {
                user.testCount = purchaseResult.refillInfo.testCount
            }
            RepositoryUser.getInstance().liveDataUser.postValue(user)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private fun getProductId(type: Int) = when(type) {
        0 -> SUB_1
        1 -> SUB_2
        2 -> SUB_3
        else -> null
    }
}
