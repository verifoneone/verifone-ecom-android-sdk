package com.verifone.connectors.klarna

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher


class KlarnaPaymentForm(ctx: Context, token:String, customerID:String, transactionID:String, klarnaReturnURL:String,resultReceiver:
                        ActivityResultLauncher<Intent>
) {
    companion object {
        const val keyClientToken = "key_client_token"
        const val keyAuthToken = "key_auth_token"
        const val keyPaymentCategory = "key_pay_category"
        const val keyReturnURL = "key_return_url"
        const val klarnaAuthorizationSuccess = 6501
        const val klarnaAuthorizationFail = 6502
        const val klarnaAuthorizationCancel = 6503
    }

    private var klarnaAuthScreen: Intent
    private var klarnaTransactionID: String
    private var klarnaCustomerID: String
    var klarnaPaymentCategory:String = "pay_now"

    private var klarnaResultReceiver:ActivityResultLauncher<Intent>
    init {

        klarnaAuthScreen = Intent(ctx, KlarnaAuthorizationScreen::class.java)
        klarnaAuthScreen.putExtra(keyClientToken,token)
        klarnaAuthScreen.putExtra(keyPaymentCategory,klarnaPaymentCategory)
        klarnaAuthScreen.putExtra(keyReturnURL,klarnaReturnURL)
        klarnaCustomerID = customerID
        klarnaTransactionID = transactionID
        klarnaResultReceiver = resultReceiver
    }

    fun displayPaymentForm() {
        klarnaResultReceiver.launch(klarnaAuthScreen)
    }
}