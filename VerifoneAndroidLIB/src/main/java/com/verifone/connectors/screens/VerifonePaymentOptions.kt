package com.verifone.connectors.screens

import android.app.Activity
import android.content.Context


class VerifonePaymentOptions(ctx:Context, displayMethods: ArrayList<String>, onPaymentOptionSelected:(paymentOption:String) -> Unit) {
    companion object{
        const val paymentOptionCard = "credit_card_3ds"
        const val paymentOptionPayPal = "payPal"
        const val paymentOptionGooglePay = "googlePay"
        const val paymentOptionKlarna = "klarna_payments"
        const val paymentOptionSwish = "swish_payments"
        const val paymentOptionMobilePay = "mobile_pay"
        const val paymentOptionVipps = "vipps_pay"
    }
    private val mCtx = ctx
    private val onSelectedMethod = onPaymentOptionSelected
    private val displayPayMethods = displayMethods
    private lateinit var payOptionsDialog:PaymentOptionsDialog

    fun showPaymentOptionList() {
        payOptionsDialog = PaymentOptionsDialog(displayPayMethods,onSelectedMethod,mCtx)
        payOptionsDialog.setOwnerActivity(mCtx as Activity)
        payOptionsDialog.show()
    }
}