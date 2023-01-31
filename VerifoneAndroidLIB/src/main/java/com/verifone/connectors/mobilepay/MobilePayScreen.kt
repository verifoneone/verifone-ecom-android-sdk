package com.verifone.connectors.mobilepay

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

internal class MobilePayScreen:AppCompatActivity() {

    companion object {
        const val keyDeepLink = "mb_deep_link"

        private const val SUCCESS_RESULT_CODE = Activity.RESULT_OK
        private const val REJECT_RESULT_CODE = 3
        private const val TRANSACTION_DONE_CODE = Activity.RESULT_CANCELED
        private const val FAILED_RESULT_CODE = 1
        private const val UNKNOWN_RESULT_CODE = 2


    }
    private val returnResultContract = createContract()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tempLink = intent.getStringExtra(keyDeepLink)?:""

        if (tempLink.isNotEmpty()) {
            openMobilePay(tempLink)
        }
        else{
            setResult(MobilepayManager.resultUnknown)
            finish()
        }
    }

    private fun createContract(): ActivityResultLauncher<Intent> {
        val mobilePayContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            when (it.resultCode) {
                SUCCESS_RESULT_CODE -> {
                    setResult(MobilepayManager.transactionSuccess)
                    finish()
                }
                TRANSACTION_DONE_CODE -> {
                    setResult(MobilepayManager.transactionFlowDone)
                    finish()
                }
                FAILED_RESULT_CODE -> {
                    setResult(MobilepayManager.transactionFailed)
                    finish()
                }
                UNKNOWN_RESULT_CODE -> {
                    setResult(MobilepayManager.resultUnknown)
                    finish()
                }
            }
        }
        return mobilePayContract
    }

    private fun openMobilePay(deepLink: String) {
        try {
            val mbPay = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(deepLink)
            }
            returnResultContract.launch(mbPay)

        } catch (exception: ActivityNotFoundException) {
            // Inform the user that MobilePay is not installed or use these links to redirect the user to Play Store:
            val urlPlayStore = "market://details?id=dk.danskebank.mobilepay"
            val playStoreScreen = Intent("android.intent.action.VIEW", Uri.parse(urlPlayStore))
            startActivity(playStoreScreen)
        }
    }
}