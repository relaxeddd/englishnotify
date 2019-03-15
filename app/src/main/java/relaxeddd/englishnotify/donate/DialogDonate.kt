package relaxeddd.englishnotify.donate

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import kotlinx.android.synthetic.main.layout_donate.view.*
import relaxeddd.englishnotify.R
import relaxeddd.englishnotify.common.*
import relaxeddd.englishnotify.dialogs.refill.DialogRefill
import relaxeddd.englishnotify.donate.utils.*
import relaxeddd.englishnotify.model.Cache

/**
 * Created by Relax on 26.03.2018.
 */
class DialogDonate : DialogFragment(), IabBroadcastReceiver.IabBroadcastListener, View.OnClickListener {

    companion object {
        private const val SKU_COINS_0 = "coins_500_1"
        private const val SKU_COINS_1 = "coins_1200_1"
        private const val SKU_COINS_2 = "coins_3750_1"
        private const val SKU_COINS_3 = "coins_6500_1"
        private const val SKU_COINS_4 = "coins_13500_1"

        private const val SKU_MEDALS_0 = "medals_0"
        private const val SKU_MEDALS_1 = "medals_1"
        private const val SKU_MEDALS_2 = "medals_2"
        private const val SKU_MEDALS_3 = "medals_3"
        private const val SKU_MEDALS_4 = "medals_4"

        private const val REQUEST_PURCHASE = 10002
    }


    private lateinit var mImageBigAttention: ImageView
    private var mHelper: IabHelper? = null
    private var mBroadcastReceiver: IabBroadcastReceiver? = null
    private var mResultError: IabResult? = null
    private var mProgressDialog: ProgressDialog? = null

    private var mGotInventoryListener: IabHelper.QueryInventoryFinishedListener = IabHelper.QueryInventoryFinishedListener { result, inventory ->
        if (mHelper == null) return@QueryInventoryFinishedListener
        if (result.isFailure) {
            showToast("Failed to query inventory: " + result)
            return@QueryInventoryFinishedListener
        }

        requestVerify(inventory.getPurchase(SKU_COINS_0))
        requestVerify(inventory.getPurchase(SKU_COINS_1))
        requestVerify(inventory.getPurchase(SKU_COINS_2))
        requestVerify(inventory.getPurchase(SKU_COINS_3))
        requestVerify(inventory.getPurchase(SKU_COINS_4))
        requestVerify(inventory.getPurchase(SKU_MEDALS_0))
        requestVerify(inventory.getPurchase(SKU_MEDALS_1))
        requestVerify(inventory.getPurchase(SKU_MEDALS_2))
        requestVerify(inventory.getPurchase(SKU_MEDALS_3))
        requestVerify(inventory.getPurchase(SKU_MEDALS_4))
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
        if (!verifyDeveloperPayload(purchase)) {
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
            Cache.removePurchase(purchase)
        } else {
            showToast("Error while consuming: " + result)
        }
        //setWaitScreen(false);
    }

    //------------------------------------------------------------------------------------------------------------------
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_donate, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view)
        initBilling()
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog.window.setLayout(width, height)
    }

    override fun onDestroy() {
        if (mBroadcastReceiver != null) activity.unregisterReceiver(mBroadcastReceiver)
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
    }

    override fun onClick(view: View) {
        SoundHelper.playSound(idSoundMenu)
        view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.anim_scale_reverse))
        if (mResultError != null) {
            showToast("Problem setting up in-app billing: " + mResultError)
            return
        }

        val payload = getDeveloperPayload()

        //setWaitScreen(true);
        try {
            when (view.id) {
                R.id.coins_0 -> mHelper?.launchPurchaseFlow(activity, SKU_COINS_0, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                R.id.coins_1 -> mHelper?.launchPurchaseFlow(activity, SKU_COINS_1, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                R.id.coins_2 -> mHelper?.launchPurchaseFlow(activity, SKU_COINS_2, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                R.id.coins_3 -> mHelper?.launchPurchaseFlow(activity, SKU_COINS_3, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                R.id.coins_4 -> mHelper?.launchPurchaseFlow(activity, SKU_COINS_4, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                R.id.medals_0 -> mHelper?.launchPurchaseFlow(activity, SKU_MEDALS_0, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                R.id.medals_1 -> mHelper?.launchPurchaseFlow(activity, SKU_MEDALS_1, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                R.id.medals_2 -> mHelper?.launchPurchaseFlow(activity, SKU_MEDALS_2, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                R.id.medals_3 -> mHelper?.launchPurchaseFlow(activity, SKU_MEDALS_3, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
                R.id.medals_4 -> mHelper?.launchPurchaseFlow(activity, SKU_MEDALS_4, REQUEST_PURCHASE, mPurchaseFinishedListener, payload)
            }
        } catch (e: IabHelper.IabAsyncInProgressException) {
            showToast(R.string.another_operation)
            //setWaitScreen(false);
        } catch (e: IllegalStateException) {
            showToast(R.string.loading)
            //setWaitScreen(false);
        }
    }

    //------------------------------------------------------------------------------------------------------------------
    private fun initView(view: View) {
        mImageBigAttention = view.image_big_attention_dialog_donate
        view.title_main_rules.typeface = Decorator.typeface
        view.button_donate_ok.setOnClickListener({
            SoundHelper.playSound(idSoundClick)
            dismiss()
        })
        view.coins_0.setOnClickListener(this)
        view.coins_1.setOnClickListener(this)
        view.coins_2.setOnClickListener(this)
        view.coins_3.setOnClickListener(this)
        view.coins_4.setOnClickListener(this)
        view.medals_0.setOnClickListener(this)
        view.medals_1.setOnClickListener(this)
        view.medals_2.setOnClickListener(this)
        view.medals_3.setOnClickListener(this)
        view.medals_4.setOnClickListener(this)
    }

    private fun initBilling() {
        val base64EncodedPublicKey = getBillingPublicKey()
        val helper = IabHelper(activity, base64EncodedPublicKey)

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

            mBroadcastReceiver = IabBroadcastReceiver(this@DialogDonate)
            val broadcastFilter = IntentFilter(IabBroadcastReceiver.ACTION)
            activity.registerReceiver(mBroadcastReceiver, broadcastFilter)

            try {
                helper.queryInventoryAsync(mGotInventoryListener)
            } catch (e: IabHelper.IabAsyncInProgressException) {
                showToast("Error querying inventory. Another async operation in progress.")
            }
        }
    }

    private fun requestVerify(purchase: Purchase?) {
        if (purchase == null || !isNetworkAvailable()) return

        Cache.userInventory.add(purchase)

        Cache.requestVerifyPurchase(purchase.token, purchase.signature, purchase.originalJson, purchase.sku, { receivedPurchase ->
            if (receivedPurchase.result.code == SERVER_RESULT_OK) {
                onPurchaseResultSuccess(receivedPurchase)
            } else if (receivedPurchase.result.code == SERVER_ERROR_PURCHASE_ALREADY_RECEIVED) {
                consumePurchase(receivedPurchase)
            }

            dismissProgressDialog()
        })
    }

    private fun showProgressDialog() {
        val progressDialog = ProgressDialog(context, R.style.AppTheme_Dark_Dialog)

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

    private fun displayBigAttention(imageResId: Int) {
        mImageBigAttention.post({
            mImageBigAttention.setImageResource(imageResId)
            mImageBigAttention.visibility = View.VISIBLE

            val animationWinSlot = AnimationUtils.loadAnimation(context, R.anim.anim_big_attention)

            mImageBigAttention.animation = animationWinSlot
            animationWinSlot.startOffset = 1200
            animationWinSlot.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}

                override fun onAnimationEnd(animation: Animation) {
                    mImageBigAttention.visibility = View.GONE
                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
            animationWinSlot.start()
        })
    }

    private fun verifyDeveloperPayload(purchase: Purchase): Boolean {
        var payload = purchase.developerPayload
        payload += "fdnht"
        return payload.substring(0, payload.length - 5) == getDeveloperPayload()
    }

    private fun consumePurchase(purchaseResult: PurchaseResult) {
        for (purchase in Cache.userInventory) {
            if (purchase.token == purchaseResult.tokenId) {
                try {
                    mHelper?.consumeAsync(purchase, mConsumeFinishedListener)
                } catch (e: IabHelper.IabAsyncInProgressException) {
                    showToast(e.toString())
                }
            }
        }
    }

    private fun onPurchaseResultSuccess(purchaseResult: PurchaseResult) {
        consumePurchase(purchaseResult)

        if (purchaseResult.result.refillInfo != null) {
            val dialogRefill = DialogRefill()

            dialogRefill.coins = purchaseResult.result.refillInfo.coins
            dialogRefill.medals = purchaseResult.result.refillInfo.medals

            dialogRefill.show(childFragmentManager, DIALOG)
        }

        SoundHelper.playSound(idSoundWin)
    }
}