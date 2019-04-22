package relaxeddd.englishnotify.donate

import androidx.databinding.ViewDataBinding
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.model.db.AppDatabase
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

    //------------------------------------------------------------------------------------------------------------------
    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            for (purchase in purchases) {
                requestVerify(purchase)
            }
        } else if (responseCode == BillingClient.BillingResponse.USER_CANCELED) {

        } else {
            showToast(R.string.error_purchase)
        }
    }

    private fun requestSkuDetails(resultListener: ListenerResult<Boolean>) {
        val purchasesInfo = billingClient?.queryPurchases(BillingClient.SkuType.INAPP)

        if (purchasesInfo?.purchasesList?.isNotEmpty() == true) {
            for (purchase in purchasesInfo.purchasesList) {
                requestVerify(purchase)
            }
        }
        if (listSkuDetails.isNotEmpty()) {
            resultListener.onResult(true)
            return
        }

        val skuList = ArrayList<String>()
        val params = SkuDetailsParams.newBuilder()

        skuList.add(SUB_1)
        skuList.add(SUB_2)
        skuList.add(SUB_3)
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(params.build()) { responseCode, skuDetailsList ->
            if (responseCode == BillingClient.BillingResponse.OK) {
                listSkuDetails = skuDetailsList
            }
            resultListener.onResult(responseCode == BillingClient.BillingResponse.OK)
        }
    }

    fun initBilling(resultListener: ListenerResult<Boolean>) {
        if (billingClient == null) {
            billingClient = BillingClient.newBuilder(this).setListener(this).build()
        }
        if (isBillingServiceConnected) {
            requestSkuDetails(resultListener)
            return
        }

        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingClient.BillingResponse billingResponseCode: Int) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    isBillingServiceConnected = true
                    requestSkuDetails(resultListener)
                } else if (billingResponseCode == BillingClient.BillingResponse.BILLING_UNAVAILABLE) {
                    showToast("Billing unavailable")
                    resultListener.onResult(false)
                } else {
                    resultListener.onResult(false)
                }
            }
            override fun onBillingServiceDisconnected() {
                isBillingServiceConnected = false
                resultListener.onResult(false)
            }
        })
    }

    fun onChooseSub(subType: Int) {
        val productId = getProductId(subType) ?: return
        var buySkuDetails: SkuDetails? = null

        for (skuDetails in listSkuDetails) {
            if (skuDetails.sku == productId) {
                buySkuDetails = skuDetails
                break
            }
        }

        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(buySkuDetails)
            .build()
        billingClient?.launchBillingFlow(this, flowParams)
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
        billingClient?.consumeAsync(purchaseResult.tokenId) { responseCode, _ ->
            if (responseCode == BillingClient.BillingResponse.OK) {
                showToast("Purchase consumed")
            } else {
                showToast("Purchase error")
            }
        }
    }

    private suspend fun onPurchaseResultSuccess(purchaseResult: PurchaseResult) {
        consumePurchase(purchaseResult)

        val user: User? = RepositoryUser.getInstance(AppDatabase.getInstance(this).userDao()).liveDataUser.value
        val newSubTime = purchaseResult.refillInfo.subscriptionTime

        if (newSubTime != 0L && user != null) {
            user.subscriptionTime = newSubTime
            AppDatabase.getInstance(this).userDao().insert(user)
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