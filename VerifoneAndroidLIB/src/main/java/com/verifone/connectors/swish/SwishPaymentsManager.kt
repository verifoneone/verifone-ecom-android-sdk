package com.verifone.connectors.swish

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import java.net.URL
import java.net.URLEncoder

class SwishPaymentsManager(ctx:Context,swishResultLauncherParam: ActivityResultLauncher<Intent>,swishEnvironment:Int) {

    private var swishStarterName = ""
    private var mContext = ctx
    private var swishResultLauncher: ActivityResultLauncher<Intent>
    init {
        swishStarterName = if (swishEnvironment == swishEnvironmentLive) {
            swishPackageName
        } else {
            swishPackageNameSandbox
        }

        swishResultLauncher = swishResultLauncherParam
    }

    companion object {
        const val swishEnvironmentSandbox = 0
        const val swishEnvironmentLive = 1

        internal const val swishTransactionFlowDone = 2190

        private val swishPackageNameSandbox = "se.bankgirot.swish.sandbox"
        private val swishPackageName = "se.bankgirot.swish"
        private const val swishUrlScheme = "sdkverifone://?token="

    }

    fun startSwishPaymentFlow(swishToken:String,swishReturnCode:Int):Boolean {
        if (swishToken.isEmpty()) return false
        val swishUrlParam = swishUrlScheme+swishToken
        val swishPaymentScreen = Intent(mContext,SwishPaymentActivity::class.java)
        swishPaymentScreen.putExtra(SwishPaymentActivity.keySwishPackageName,swishStarterName)
        swishPaymentScreen.putExtra(SwishPaymentActivity.keySwishToken,swishToken)
        swishPaymentScreen.putExtra(SwishPaymentActivity.keyUrlSchema,swishUrlParam)
        swishPaymentScreen.putExtra(SwishPaymentActivity.keySwishReturnCode,swishReturnCode)
        swishResultLauncher.launch(swishPaymentScreen)
        return true
    }


    fun isSwishInstalled(ctx:Context):Boolean {
        return try {
            ctx.packageManager
                .getPackageInfo(swishStarterName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {

            false
        }
    }
}