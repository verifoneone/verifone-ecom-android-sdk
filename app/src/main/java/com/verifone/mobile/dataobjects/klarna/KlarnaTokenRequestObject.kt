package com.verifone.mobile.dataobjects.klarna

import com.google.gson.annotations.SerializedName

class KlarnaTokenRequestObject {
    @SerializedName("amount") var amount = 1000
    @SerializedName("auth_type") var authType = "FINAL_AUTH"
    @SerializedName("capture_now") var captureNow = false
    @SerializedName("customer") var customer = ""
    @SerializedName("redirect_url") var redirectURL = "http://2checkout.com/test"
    @SerializedName("customer_ip") var customerIP = "127.0.0.1"
    @SerializedName("currency_code") var currencyCode = "SEK"
    @SerializedName("user_agent") var userAgent = "string"
    @SerializedName("entity_id") var entityID = ""
    @SerializedName("merchant_reference") var merchantReference = "5678"
    @SerializedName("dynamic_descriptor") var dynamicDescriptor = "TEST AUTOMATION ECOM"
    @SerializedName("purchase_country") var purchaseCountry = "SE"
    @SerializedName("locale") var locale = LocaleObject()
    @SerializedName("line_items") var lineItems= arrayOf(KlarnaPurchasedItem())

}