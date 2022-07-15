package com.verifone.connectors.googlepay
import android.app.Activity

import android.content.Intent
import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.wallet.*
import com.verifone.connectors.GooglePayUtils

import org.json.JSONException
import org.json.JSONObject

class VerifoneGooglePay(onGooglePay: (googlePossible: Boolean) -> Unit, paramScreen: Activity,
                        paymentDataRequestCode: Int, paymentEnvironment:Int) {


    companion object{
        const val testEnvironment = 3
        const val productionEnvironment = 1
    }

    private var paymentsClient:PaymentsClient = GooglePayUtils.createPaymentsClient(paramScreen,paymentEnvironment)
    private var ownerScreen:Activity = paramScreen

    private var onGooglePayPossible = onGooglePay
    private lateinit var mGooglePayloadObject:WalletPayloadObject
    private var ownerActivityRequestCode:Int = paymentDataRequestCode


    fun showGooglePayIfPossible() {
        val isReadyToPayJson = GooglePayUtils.isReadyToPayRequest() ?: return
        val request = IsReadyToPayRequest.fromJson(isReadyToPayJson.toString()) ?: return
        // The call to isReadyToPay is asynchronous and returns a Task. We need to provide an
        // OnCompleteListener to be triggered when the result of the call is known.
        val task = paymentsClient.isReadyToPay(request)

        task.addOnCompleteListener { completedTask ->
            try {
                completedTask.getResult(ApiException::class.java)?.let{
                    onGooglePayPossible(it)
                }
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay1 failed", exception)
                Log.w("isReadyToPay1 message", exception.localizedMessage)
                Log.w("isReadyToPay1 reason", exception.cause.toString())
                Log.w("isReadyToPay1 mess", exception.message.toString())
            }
        }
    }

    fun requestPaymentGoogle(configObject:GooglePayConfiguration) {

        val paymentDataRequestJson = GooglePayUtils.getPaymentDataRequest(configObject)
        if (paymentDataRequestJson == null) {
            Log.e("RequestPayment", "Can't fetch or invalid payment data request")
            return
        }
        val request = PaymentDataRequest.fromJson(paymentDataRequestJson.toString())

        if (request != null) {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request), ownerScreen,ownerActivityRequestCode)
        }
    }

    fun requestPaymentGoogle(requestObject:JSONObject) {
        val request = PaymentDataRequest.fromJson(requestObject.toString())

        if (request != null) {
            AutoResolveHelper.resolveTask(
                paymentsClient.loadPaymentData(request), ownerScreen,ownerActivityRequestCode)
        }
    }



    fun parseGooglePayload(walletPayload: Intent):WalletPayloadObject {
        PaymentData.getFromIntent(walletPayload)?.let {
                response->handleGooglePayload(response)
        }
        return mGooglePayloadObject
    }

    /**
     * PaymentData response object contains the payment information, as well as any additional
     * requested information, such as billing and shipping address.
     *
     * @param paymentData A response object returned by Google after a payer approves payment.
     * @see [Payment
     * Data](https://developers.google.com/pay/api/android/reference/object.PaymentData)
     */
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