package com.verifone.connectors.googlepay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.verifone.connectors.GooglePayUtils
import com.verifone.connectors.R
import org.json.JSONException
import org.json.JSONObject
import java.lang.Exception

internal class GooglePayStarterScreen:AppCompatActivity() {

    companion object {
        const val keyConfigObject = "googlePayConfigObject"
        const val keyCheckGooglePayPossible = "googlePayCheck"
        const val keyMerchantResultCode = "googlePayResultCode"
        const val keyHasResultLauncher = "hasResLauncher"

        private fun handleGooglePayload(paymentData: PaymentData):WalletPayloadObject {
            var tempPayloadObject:WalletPayloadObject
            val paymentInformation = paymentData.toJson()
            try {
                // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
                val paymentMethodData = JSONObject(paymentInformation).getJSONObject("paymentMethodData")

                val tokenPayload = paymentMethodData
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
                tempPayloadObject = WalletPayloadObject(paramSignature,paramProtocol,paramSignedMessage,paramSignedKey,paramSignaturesArray)

            } catch (e: JSONException) {
                Log.e("handlePayment", "Error: $e")
                tempPayloadObject = WalletPayloadObject()
            }
            return tempPayloadObject
        }

        fun parseGooglePayload(walletPayload: Intent):WalletPayloadObject {
            var googlePayloadObject = WalletPayloadObject()
            PaymentData.getFromIntent(walletPayload)?.let {
                    response-> googlePayloadObject = handleGooglePayload(response)
            }
            return googlePayloadObject
        }
    }

    private lateinit var paymentsClient: PaymentsClient
    private var googlePayRequestCode = 7418
    private var merchantResultCode = -1
    private lateinit var googlePayConfigObject:JSONObject
    private var googlePayChecked = false
    private var hasResultLauncher = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_Transparent)
        googlePayChecked = intent.getBooleanExtra(keyCheckGooglePayPossible,false)
        try {
            googlePayConfigObject = JSONObject(intent.getStringExtra(keyConfigObject).toString())
        } catch (e:Exception) {
            finish()
        }
        paymentsClient = GooglePayUtils.createPaymentsClient(this,VerifoneGooglePay.testEnvironment)
        if (googlePayChecked){
            startGooglePay()
        } else {
            checkGooglePayPossible()
        }
        merchantResultCode = intent.getIntExtra(keyMerchantResultCode,-1)
        hasResultLauncher = intent.getBooleanExtra(keyHasResultLauncher,false)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (requestCode) {
            googlePayRequestCode -> {
                when (resultCode) {
                    RESULT_OK -> {
                        data?.let { intent ->
                            if (hasResultLauncher){
                                setResult(merchantResultCode,intent)
                                finish()
                            } else {
                                VerifoneGooglePay.onGooglePayConfirmation(parseGooglePayload(intent),merchantResultCode)
                                finish()
                            }
                        }
                    }
                    RESULT_CANCELED -> {
                        finish()
                    }

                    1 -> {
                        finish()
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun startGooglePay(){
        val request = PaymentDataRequest.fromJson(googlePayConfigObject.toString())


        if (request != null) {
            AutoResolveHelper.resolveTask(paymentsClient.loadPaymentData(request), this,googlePayRequestCode)
        }
    }


    fun checkGooglePayPossible() {
        val isReadyToPayJson = GooglePayUtils.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return
        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = paymentsClient.isReadyToPay(request)

        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let{
                    if (it) {
                        startGooglePay()
                    } else {
                        finish()
                    }
                }
            } catch (exception: ApiException) {

            }
        }
    }


}