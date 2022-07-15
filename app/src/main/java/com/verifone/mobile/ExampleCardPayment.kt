package com.verifone.mobile

import android.content.Context
import com.verifone.connectors.threeds.dataobjects.ThreedsValidationData
import com.verifone.mobile.controllers.CardPaymentController
import com.verifone.mobile.responses.cardpayments.CardPaymentResponse

class ExampleCardPayment(ctx: Context, onCardPaymentFlow: (response: CardPaymentResponse) -> Unit) {

    private val mCardPayController = CardPaymentController(ctx,onCardPaymentFlow)

    fun flowCardPayment(encryptedCard:String,payment_provider_contract:String, amount:Double,auth_type:String,capture_now:Boolean ,merchant_reference:String, card_brand:String, currency_code:String, dynamic_descriptor:String,keyAlias:String,threedData: ThreedsValidationData?) {
        mCardPayController.startCardPaymentDataRequest(encryptedCard,payment_provider_contract,amount,auth_type,capture_now,merchant_reference,card_brand,currency_code,dynamic_descriptor,keyAlias,threedData)
    }

    fun flowCardPaymentNoThreeds(encryptedCard:String,payment_provider_contract:String, amount:Double,auth_type:String,capture_now:Boolean ,merchant_reference:String, card_brand:String, currency_code:String, dynamic_descriptor:String,keyAlias:String,threedData: ThreedsValidationData?) {
        mCardPayController.startCardPaymentDataRequest(encryptedCard,payment_provider_contract,amount,auth_type,capture_now,merchant_reference,card_brand,currency_code,dynamic_descriptor,keyAlias,threedData)
    }

    fun flowTokenPayment(reuseToken:String="",payment_provider_contract:String, amount:Double,auth_type:String,capture_now:Boolean ,merchant_reference:String, card_brand:String, currency_code:String, dynamic_descriptor:String,keyAlias:String,threedData: ThreedsValidationData?) {
        mCardPayController.startTokenPaymentDataRequest(reuseToken,payment_provider_contract,amount,auth_type,capture_now,merchant_reference,card_brand,currency_code,dynamic_descriptor,keyAlias,threedData)
    }

}