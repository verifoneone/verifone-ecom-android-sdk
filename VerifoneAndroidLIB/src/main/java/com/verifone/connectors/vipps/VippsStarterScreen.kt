package com.verifone.connectors.vipps

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

internal class VippsStarterScreen:AppCompatActivity() {

    private var returnCode = -1

    companion object {
        const val vippsPackageName = "no.dnb.vipps"
        const val vippsRequestCode: Int = 1011
        const val keyVippsDeepLinkUrl = "vippsDeepLink"
        const val keyVippsReturnCode = "vippsReturnCode"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val starterDeepLink = intent.getStringExtra(keyVippsDeepLinkUrl)?:""
        returnCode = intent.getIntExtra(keyVippsReturnCode,-1)
        if (starterDeepLink.isNotEmpty()) {
            if (!openVippsApp(starterDeepLink)){
                setResult(VippsPaymentsManager.vippsTransactionNotStarted)
                finish()
            }
        }
        else finish()
    }

    private fun openVippsApp(deeplinkUrl: String):Boolean {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplinkUrl))
            startActivityForResult(intent, vippsRequestCode)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        } catch (e:ActivityNotFoundException){
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (returnCode == -1) {
            setResult(VippsPaymentsManager.vippsTransactionFlowDone)
        } else {
            setResult(returnCode)
        }
        finish()
    }
}