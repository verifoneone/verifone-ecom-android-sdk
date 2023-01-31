package com.verifone.connectors.googlepay
import android.app.Activity
import android.content.Context

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.verifone.connectors.GooglePayUtils

import org.json.JSONException
import org.json.JSONObject

class VerifoneGooglePay {

    private var hasParentActivity:Boolean = false

    constructor (ctx: Context, onGooglePay: (googlePossible: Boolean) -> Unit, paramScreen: Activity,
                 paymentEnvironment:Int) {
        hasParentActivity = true
        paymentsClient= GooglePayUtils.createPaymentsClient(paramScreen,paymentEnvironment)
        onGooglePayPossible = onGooglePay
        mCtx = ctx
    }

    constructor(ctx: Context,
                paymentEnvironment:Int) {
        mCtx= ctx
        hasParentActivity = false
    }
    companion object {
        const val testEnvironment = 3
        const val productionEnvironment = 1
        lateinit var onGooglePayConfirmation: (walletPayload:WalletPayloadObject,resultCode:Int) -> Unit
        fun parseGooglePayload(walletPayload: Intent):WalletPayloadObject {
            return GooglePayStarterScreen.parseGooglePayload(walletPayload)
        }
    }

    private lateinit var paymentsClient:PaymentsClient
    private var mCtx:Context
    private lateinit var onGooglePayPossible:(googlePayPossible: Boolean) -> Unit
    private lateinit var mGooglePayloadObject:WalletPayloadObject

    fun showGooglePayIfPossible() {
        val isReadyToPayJson = GooglePayUtils.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return
        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        if (!hasParentActivity) return
        val task = paymentsClient.isReadyToPay(request)

        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let{
                    onGooglePayPossible(it)
                }
            } catch (exception: ApiException) {

            }
        }
    }

    private fun startGooglePayScreen(resultCode:Int, configObject:GooglePayConfiguration){
        val googlePayStarter = Intent(mCtx,GooglePayStarterScreen::class.java)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyConfigObject,GooglePayUtils.getPaymentDataRequest(configObject).toString())
        googlePayStarter.putExtra(GooglePayStarterScreen.keyCheckGooglePayPossible,hasParentActivity)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyMerchantResultCode,resultCode)
        mCtx.startActivity(googlePayStarter)
    }

    private fun startGooglePayScreen(resultCode:Int, configObject:JSONObject){
        val googlePayStarter = Intent(mCtx,GooglePayStarterScreen::class.java)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyConfigObject,configObject.toString())
        googlePayStarter.putExtra(GooglePayStarterScreen.keyCheckGooglePayPossible,hasParentActivity)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyMerchantResultCode,resultCode)
        mCtx.startActivity(googlePayStarter)
    }

    fun requestGooglePayment(googlePaySessionID:Int, configObject:GooglePayConfiguration, onGooglePayConfirmed: (walletPayload:WalletPayloadObject,sessionID:Int) -> Unit) {
        onGooglePayConfirmation = onGooglePayConfirmed
        startGooglePayScreen(googlePaySessionID,configObject)
    }

    fun requestGooglePayment(googlePaySessionID:Int, requestObject:JSONObject, onGooglePayConfirmed: (walletPayload:WalletPayloadObject,sessionID:Int) -> Unit) {
        onGooglePayConfirmation = onGooglePayConfirmed
        startGooglePayScreen(googlePaySessionID,requestObject)
    }

    fun requestGooglePayment(configObject: GooglePayConfiguration,resultCode:Int,googlePayResultLauncherParam: ActivityResultLauncher<Intent>){
        val googlePayStarter = Intent(mCtx,GooglePayStarterScreen::class.java)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyConfigObject,GooglePayUtils.getPaymentDataRequest(configObject).toString())
        googlePayStarter.putExtra(GooglePayStarterScreen.keyCheckGooglePayPossible,hasParentActivity)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyHasResultLauncher,true)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyMerchantResultCode,resultCode)
        googlePayResultLauncherParam.launch(googlePayStarter)
    }

    fun requestGooglePayment(configObject: JSONObject,resultCode:Int,googlePayResultLauncherParam: ActivityResultLauncher<Intent>){
        val googlePayStarter = Intent(mCtx,GooglePayStarterScreen::class.java)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyConfigObject,configObject.toString())
        googlePayStarter.putExtra(GooglePayStarterScreen.keyHasResultLauncher,true)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyCheckGooglePayPossible,hasParentActivity)
        googlePayStarter.putExtra(GooglePayStarterScreen.keyMerchantResultCode,resultCode)
        googlePayResultLauncherParam.launch(googlePayStarter)
    }

    private fun getGooglePayload(walletPayload: Intent):WalletPayloadObject {
        PaymentData.getFromIntent(walletPayload)?.let {
                response->handleGooglePayload(response)
        }
        return mGooglePayloadObject
    }


    private fun handleGooglePayload(paymentData: PaymentData) {
        val paymentInformation = paymentData.toJson() ?: return
        try {
            // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
            val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")

            var tokenPayload = paymentMethodData
                .getJSONObject("tokenizationData").getString("token")
            val tokenObject = JSONObject(tokenPayload)
            val paramSignature = tokenObject.getString("signature")
            val paramProtocol = tokenObject.getString("protocolVersion")
            val paramSignedMessage = tokenObject.getString("signedMessage")
            val tempObject = tokenObject.getJSONObject("intermediateSigningKey")
            val paramSignaturesOBJ = tempObject.getJSONArray("signatures")
            var paramSignaturesArray:Array<String>

            paramSignaturesOBJ.let {paramArr->
                val listSignatures = Array(paramArr.length()) {
                    paramArr.getString(it)
                }
                paramSignaturesArray = listSignatures
            }

            val paramSignedKey = tempObject.get("signedKey") as String
            mGooglePayloadObject = WalletPayloadObject(paramSignature,paramProtocol,paramSignedMessage,paramSignedKey,paramSignaturesArray)

        } catch (e: JSONException) {
            Log.e("handlePayment", "Error: $e")
            mGooglePayloadObject = WalletPayloadObject()
        }

    }

}