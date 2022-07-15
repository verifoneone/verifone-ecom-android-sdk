package com.verifone.mobile.dataobjects.cardpayment

import com.google.gson.annotations.SerializedName
import com.verifone.connectors.threeds.dataobjects.ThreedsValidationData

class TokenPaymentObject {
    constructor(reuseT:String, payment_provider_contract:String, amount_val:Double, auth_type:String, capture_now:Boolean, merchant_reference:String, card_brand:String, currency_code:String, dynamic_descriptor:String, threed_authentication: ThreedsValidationData?, keyAlias:String) {
        paymentProviderContract = payment_provider_contract
        amount = (amount_val).toInt()*100
        authType = auth_type
        captureNow = capture_now
        merchantReference = merchant_reference
        cardBrand = card_brand
        dynamicDescriptor = dynamic_descriptor

        reuseToken = reuseT
        currencyCode = currency_code
        threedAuthentication = threed_authentication
        publicKeyAlias = keyAlias
    }

    @SerializedName("reuse_token")
    var reuseToken = ""

    @SerializedName("public_key_alias")
    private var publicKeyAlias = ""
    @SerializedName("amount")
    private var amount = 1
    @SerializedName("currency_code")
    private var currencyCode = ""
    @SerializedName("merchant_reference")
    private var merchantReference = ""

    @SerializedName("payment_provider_contract")
    private var paymentProviderContract:String=""
    @SerializedName("auth_type")
    private var authType = ""
    @SerializedName("brand_choice")
    var brandChoice = "MERCHANT"
    @SerializedName("capture_now")
    private var captureNow= true
    @SerializedName("card_brand")
    private var cardBrand = ""
    @SerializedName("customer_ip")
    var customerIP = "127.0.0.1"

    @SerializedName("dynamic_descriptor")
    private var dynamicDescriptor = ""

    //@SerializedName("threed_authentication")
    private var threedAuthentication: ThreedsValidationData? = ThreedsValidationData("","","","","","","")
    @SerializedName("shopper_interaction")
    var shopperInteraction = "ECOMMERCE"


}