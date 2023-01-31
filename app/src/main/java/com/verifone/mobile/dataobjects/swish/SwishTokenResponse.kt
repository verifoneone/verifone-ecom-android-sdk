package com.verifone.mobile.dataobjects.swish

import com.google.gson.annotations.SerializedName

class SwishTokenResponse {

    @SerializedName("amount")
    var amount = 0

    @SerializedName("blocked")
    var blocked = false

    @SerializedName("created_at")
    var createdAt = ""

    @SerializedName("merchant_reference")
    var merchantReference = ""

    @SerializedName("status")
    var status = ""

    @SerializedName("shopper_interaction")
    var shopperInteraction = ""

    @SerializedName("id")
    var id = ""

    @SerializedName("processor")
    var processor = ""

    @SerializedName("payment_product")
    var paymentProduct = ""

    @SerializedName("payment_product_type")
    var paymentProductType = ""

    @SerializedName("payment_request_token")
    var paymentRequestToken = ""
}