package com.verifone.connectors.vipps

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher

class VippsPaymentsManager {
    companion object {
        const val vippsTransactionFlowDone = 4001
        const val vippsTransactionNotStarted = 4002
        fun showVippsAuthorizeScreen(ctx:Context,resultReceiver:ActivityResultLauncher<Intent>,deeplinkUrl: String) {
            val intent = Intent(ctx,VippsStarterScreen::class.java)
            intent.putExtra(VippsStarterScreen.keyVippsDeepLinkUrl,deeplinkUrl)
            resultReceiver.launch(intent)
        }

        fun showVippsAuthorizeScreen(ctx:Context,vippsReturnCode:Int,resultReceiver:ActivityResultLauncher<Intent>,deeplinkUrl: String) {
            val intent = Intent(ctx,VippsStarterScreen::class.java)
            intent.putExtra(VippsStarterScreen.keyVippsDeepLinkUrl,deeplinkUrl)
            intent.putExtra(VippsStarterScreen.keyVippsReturnCode,vippsReturnCode)
            resultReceiver.launch(intent)
        }
    }
}