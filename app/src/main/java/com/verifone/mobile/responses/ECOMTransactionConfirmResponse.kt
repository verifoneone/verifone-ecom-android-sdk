package com.verifone.mobile.responses

import com.google.gson.annotations.SerializedName

class ECOMTransactionConfirmResponse {

    @SerializedName("id")
    var id = ""

    @SerializedName("amount")
    var amount = ""

    @SerializedName("currency_code")
    var currencyCode = ""

    @SerializedName("created_at")
    var createdAt = ""

    @SerializedName("customer")
    var customer = ""

    @SerializedName("user_agent")
    var userAgent = ""

    @SerializedName("merchant_reference")
    var merchantReference = ""

    @SerializedName("payment_product")
    var paymentProduct = ""

    @SerializedName("payment_product_type")
    var paymentProductType = "MASTERCARD"

    @SerializedName("status")
    var transactionStatus = ""

    @SerializedName("processor_reference")
    var processorReference = ""

}