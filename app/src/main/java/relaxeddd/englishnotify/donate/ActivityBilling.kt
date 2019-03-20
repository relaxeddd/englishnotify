package relaxeddd.englishnotify.donate

import android.app.ProgressDialog
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

abstract class ActivityBilling<VM : ViewModelBase, B : ViewDataBinding> : ActivityBase<VM, B>(),
    PurchasesUpdatedListener {

    companion object {
        private const val SUB_1 = "sub_1"
        private const val SUB_2 = "sub_2"
        private const val SUB_3 = "sub_3"

        private const val REQUEST_PURCHASE = 10002
    }

    private var billingClient: BillingClient? = null
    private var isBillingServiceConnected = false
    private var listSkuDetails: List<SkuDetails> = ArrayList()

    /*private var mHelper: IabHelper? = null
    private var mBroadcastReceiver: IabBroadcastReceiver? = null
    private var mResultError: IabResult? = null*/
    private var mProgressDialog: ProgressDialog? = null

    //private var purchases = HashSet<Purchase>()

    /*private var mGotInventoryListener: IabHelper.QueryInventoryFinishedListener = IabHelper.QueryInventoryFinishedListener { result, inventory ->
        if (mHelper == null) return@QueryInventoryFinishedListener
        if (result.isFailure) {
            showToast("Failed to query inventory: " + result)
            return@QueryInventoryFinishedListener
        }

        requestVerify(inventory.getPurchase(SUB_1))
        requestVerify(inventory.getPurchase(SUB_2))
        requestVerify(inventory.getPurchase(SUB_3))
        //setWaitScreen(false);
    }

    private var mPurchaseFinishedListener: IabHelper.OnIabPurchaseFinishedListener = IabHelper.OnIabPurchaseFinishedListener { result, purchase ->
        if (mHelper == null) return@OnIabPurchaseFinishedListener
        if (result.isFailure) {
            if (result.response != -1005) { //user cancelled
                showToast(result.message)
            }
            //setWaitScreen(false);
            return@OnIabPurchaseFinishedListener
        }
        if (!verify(purchase)) {
            showToast("Error purchasing. Authenticity verification failed.")
            //setWaitScreen(false);
            return@OnIabPurchaseFinishedListener
        }

        showProgressDialog()
        requestVerify(purchase)
    }

    private var mConsumeFinishedListener: IabHelper.OnConsumeFinishedListener = IabHelper.OnConsumeFinishedListener { purchase, result ->
        if (mHelper == null) return@OnConsumeFinishedListener
        if (result.isSuccess) {
            removePurchase(purchase)
        } else {
            showToast("Error while consuming: " + result)
        }
        //setWaitScreen(false);
    }*/

    //------------------------------------------------------------------------------------------------------------------
    /*override fun onDestroy() {
        if (mBroadcastReceiver != null) unregisterReceiver(mBroadcastReceiver)
        try {
            mHelper?.disposeWhenFinished()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
        mHelper = null

        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (mHelper?.handleActivityResult(requestCode, resultCode, data) != true) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun receivedBroadcast() {
        try {
            mHelper?.queryInventoryAsync(mGotInventoryListener)
        } catch (e: IabHelper.IabAsyncInProgressException) {
            showToast("Error querying inventory. Another async operation in progress.")
        }
    }*/

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
                }
            }
            override fun onBillingServiceDisconnected() {
                isBillingServiceConnected = false
                resultListener.onResult(false)
            }
        })

        /*val base64EncodedPublicKey = codeRequest()
        val helper = IabHelper(this, base64EncodedPublicKey)

        mHelper = helper
        helper.enableDebugLogging(false)
        helper.startSetup { result ->
            if (!result.isSuccess) {
                mResultError = result
                return@startSetup
            } else {
                mResultError = null
            }

            if (mHelper == null) {
                return@startSetup
            }

            mBroadcastReceiver = IabBroadcastReceiver(this)
            val broadcastFilter = IntentFilter(IabBroadcastReceiver.ACTION)
            registerReceiver(mBroadcastReceiver, broadcastFilter)

            try {
                helper.queryInventoryAsync(mGotInventoryListener)
            } catch (e: IabHelper.IabAsyncInProgressException) {
                showToast("Error querying inventory. Another async operation in progress.")
            }
        }*/
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

        /*if (mResultError != null) {
            showToast("Problem setting up in-app billing: " + mResultError)
            return
        }

        val payload = codeRequest()

        //setWaitScreen(true);
        try {
            when(subType) {
                0 -> mHelper?.launchPurchaseFlow(this, SUB_1, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                1 -> mHelper?.launchPurchaseFlow(this, SUB_2, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                2 -> mHelper?.launchPurchaseFlow(this, SUB_3, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
            }
        } catch (e: IabHelper.IabAsyncInProgressException) {
            showToast(R.string.another_operation)
            //setWaitScreen(false);
        } catch (e: IllegalStateException) {
            showToast(R.string.loading)
            //setWaitScreen(false);
        }*/
    }

    private fun requestVerify(purchase: Purchase?) {
        if (purchase == null || !isNetworkAvailable()) return

        CoroutineScope(Dispatchers.Main).launch {
            val firebaseUser = RepositoryCommon.getInstance().firebaseUser
            val tokenId = RepositoryCommon.getInstance().tokenId

            val purchaseResult = ApiHelper.requestVerifyPurchase(firebaseUser, tokenId, purchase.purchaseToken,
                purchase.signature, purchase.originalJson, purchase.sku)

            if (purchaseResult.result.isSuccess()) {
                onPurchaseResultSuccess(purchaseResult)
            } else if (purchaseResult.result.code == RESULT_PURCHASE_ALREADY_RECEIVED) {
                consumePurchase(purchaseResult)
            }
        }
    }

    private fun consumePurchase(purchaseResult: PurchaseResult) {
        billingClient?.consumeAsync(purchaseResult.purchase?.tokenId) { responseCode, outToken ->
            if (responseCode == BillingClient.BillingResponse.OK) {
                showToast("Purchase consumed")
            }
        }

        /*for (purchase in purchases) {
            if (purchase.token == purchaseResult.purchase?.tokenId) {
                try {
                    mHelper?.consumeAsync(purchase, mConsumeFinishedListener)
                } catch (e: IabHelper.IabAsyncInProgressException) {
                    showToast(e.toString())
                }
            }
        }*/
    }

    private suspend fun onPurchaseResultSuccess(purchaseResult: PurchaseResult) {
        consumePurchase(purchaseResult)

        val user: User? = RepositoryUser.getInstance(AppDatabase.getInstance(this).userDao()).liveDataUser.value
        val newSubTime = purchaseResult.purchase?.refillInfo?.subscriptionTime

        if (newSubTime != null && newSubTime != 0L && user != null) {
            user.subscriptionTime = newSubTime
            AppDatabase.getInstance(this).userDao().insert(user)
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    /*private fun verify(purchase: Purchase): Boolean {
        var payload = purchase.developerPayload
        payload += "fdnht"
        return payload.substring(0, payload.length - 5) == codeRequest()
    }*/

    private fun showProgressDialog() {
        val progressDialog = ProgressDialog(this, R.style.Base_Theme_AppCompat_Light_Dialog_Alert)

        mProgressDialog = progressDialog
        progressDialog.isIndeterminate = true
        progressDialog.setCancelable(false)
        progressDialog.setMessage(getString(R.string.please_wait))
        progressDialog.show()
    }

    private fun dismissProgressDialog() {
        if (mProgressDialog?.isShowing == true) {
            mProgressDialog?.dismiss()
        }
    }

    /*private fun removePurchase(purchase: Purchase) {
        purchases = HashSet(purchases.filter { purchase.itemType != it.itemType })
    }*/

    private fun getProductId(type: Int) = when(type) {
        0 -> SUB_1
        1 -> SUB_2
        2 -> SUB_3
        else -> null
    }
}