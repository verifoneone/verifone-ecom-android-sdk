package com.verifone.mobile.dataobjects.googlepayments

import com.google.gson.annotations.SerializedName
import com.verifone.connectors.googlepay.WalletPayloadObject

class GooglePayObject {
    constructor(payment_provider_contract:String, amount_val:Int,auth_type:String,capture_now:Boolean , customer_ip:String,merchant_reference:String, card_brand:String, shopper_interaction:String, currency_code:String, dynamic_descriptor:String,wallet_type:String,wallet_payload: WalletPayloadObject) {
        paymentProviderContract = payment_provider_contract
        amount = amount_val
        authType = auth_type
        captureNow = capture_now
        customerIP = customer_ip
        merchantReference = merchant_reference
        cardBrand = card_brand
        shopperInteraction = shopper_interaction
        currencyCode = currency_code
        dynamicDescriptor = dynamic_descriptor
        walletType = wallet_type
        walletPayload = wallet_payload
        //threedAuthentication = threed_authentication
    }
    @SerializedName("payment_provider_contract")
    private var paymentProviderContract:String=""
    @SerializedName("amount")
    private var amount = 1
    @SerializedName("auth_type")
    private var authType = "FINAL_AUTH"
    @SerializedName("capture_now")
    private var captureNow= true
    @SerializedName("customer_ip")
    private var customerIP =  ""
    @SerializedName("merchant_reference")
    private var merchantReference = ""
    @SerializedName("card_brand")
    private var cardBrand = ""
    @SerializedName("shopper_interaction")
    private var shopperInteraction = ""
    @SerializedName("currency_code")
    private var currencyCode = ""
    @SerializedName("dynamic_descriptor")
    private var dynamicDescriptor = ""

    @SerializedName("sca_compliance_level")
    private var scaComplianceLevel="NONE"

    //@SerializedName("threed_authentication")
    //private var threedAuthentication = ThreedAuthentication()

    @SerializedName("wallet_type")
    private var walletType = "GOOGLE_PAY"

    @SerializedName("wallet_payload")
    private var walletPayload: WalletPayloadObject

}