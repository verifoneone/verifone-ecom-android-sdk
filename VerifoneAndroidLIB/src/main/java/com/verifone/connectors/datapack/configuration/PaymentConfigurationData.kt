package com.verifone.connectors.datapack.configuration

import android.content.Context

class PaymentConfigurationData {
    constructor(ctx:Context, price:String="", publicKey:String, showStoredCard:Boolean = false,displayUICustomization: FormUICustomizationData){
        activityContext = ctx
        displayPrice = price
        merchantPublicKey = publicKey
        showStoredCardOption = showStoredCard
        displayCustomization = displayUICustomization
    }

    constructor(ctx:Context, publicKey:String, showStoredCard:Boolean = false,displayUICustomization: FormUICustomizationData,payButtonString:String){
        activityContext = ctx
        payButtonText = payButtonString
        merchantPublicKey = publicKey
        showStoredCardOption = showStoredCard
        displayCustomization = displayUICustomization
    }

    constructor(ctx:Context, publicKey:String) {
        activityContext = ctx
        merchantPublicKey = publicKey
    }

    var activityContext:Context
    var displayPrice:String=""
    var payButtonText:String = ""
    var merchantPublicKey:String
    var showStoredCardOption:Boolean = false
    var displayCustomization: FormUICustomizationData = FormUICustomizationData()
}