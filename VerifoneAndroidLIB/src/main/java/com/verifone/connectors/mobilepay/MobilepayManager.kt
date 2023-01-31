package com.verifone.connectors.mobilepay

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher


class MobilepayManager {
    companion object {
        const val transactionFlowDone = 20
        const val transactionSuccess = 10
        const val transactionRejected = 11
        const val transactionFailed = 12
        const val resultUnknown = 13


        fun openMobilePay(ctx:Context,resultLauncher:ActivityResultLauncher<Intent>,deepLink: String) {
            val mobilePayScreen = Intent(ctx,MobilePayScreen::class.java)
            mobilePayScreen.putExtra(MobilePayScreen.keyDeepLink,deepLink)
            resultLauncher.launch(mobilePayScreen)
        }
    }
}