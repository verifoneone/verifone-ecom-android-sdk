package com.verifone.mobile.dataobjects.swish

import com.google.gson.annotations.SerializedName

class SwishTokenRequestObject {
    @SerializedName("amount")
    var amount = 1000

    @SerializedName("payment_provider_contract")
    var paymentProviderContract = ""

    @SerializedName("capture_now")
    var captureNow = 1

    @SerializedName("currency_code")
    var currencyCode = "SEK"

    @SerializedName("entity_id")
    var entityId = ""

    @SerializedName("merchant_reference")
    var merchantReference = "5690-android"

}