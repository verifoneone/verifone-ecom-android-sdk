package com.verifone.connectors.swish

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.verifone.connectors.R

internal class SwishPaymentActivity:AppCompatActivity() {
    companion object {
        const val keySwishPackageName = "swishPackageName"
        const val keyUrlSchema = "urlSchema"
        const val keySwishToken = "swishToken"
        const val keySwishReturnCode = "swishReturnCode"
    }

    var returnCode:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Transparent)
        val urlSchema = intent.getStringExtra(keyUrlSchema).toString()
        val swishPackageName = intent.getStringExtra(keySwishPackageName).toString()
        val swishToken = intent.getStringExtra(keySwishToken).toString()
        returnCode = intent.getIntExtra(keySwishReturnCode,SwishPaymentsManager.swishTransactionFlowDone)
        startSwishWithToken(this,swishToken,urlSchema,swishPackageName)
    }

    private fun startSwishWithToken(ctx: Context, swishToken:String, swishUrlScheme:String,swishPackageName:String):Boolean {

        if (swishToken == null || swishToken.length === 0 || ctx == null) {
            return false
        }

        val url: Uri = Uri.Builder()
            .scheme("swish")
            .authority("paymentrequest")
            .appendQueryParameter("token", swishToken)
            .appendQueryParameter("callbackurl", swishUrlScheme)
            .build()
        val intent = Intent(Intent.ACTION_VIEW, url)
        intent.setPackage(swishPackageName)
        try {
            startActivity(intent)
        } catch (e: Exception) {
            return false
        }
        return true
    }

    override fun onNewIntent(intentSW: Intent?) {
        super.onNewIntent(intentSW)
        parseToken()
    }

    private fun parseToken() {
        val bundle = intent?.extras
        val swishTokenTemp = bundle?.getString("swishToken")
        setResult(returnCode,Intent().putExtra(keySwishToken,swishTokenTemp))
        finish()
    }
}