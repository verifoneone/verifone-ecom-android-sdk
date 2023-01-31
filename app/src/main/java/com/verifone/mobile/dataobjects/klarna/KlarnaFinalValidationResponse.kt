package com.verifone.mobile.dataobjects.klarna

import com.google.gson.annotations.SerializedName

class KlarnaFinalValidationResponse {
    @SerializedName("id") var id = ""
    @SerializedName("instore_reference") var inStoreReference = ""
    @SerializedName("amount") var amount = 0
    @SerializedName("created_at") var createdAt = ""
    @SerializedName("merchant_reference") var merchantReference = ""
    @SerializedName("processor") var processor = ""
    @SerializedName("payment_product") var paymentProduct = ""
    @SerializedName("payment_product_type") var paymentProductType = ""
    @SerializedName("status") var status = ""
    @SerializedName("shopper_interaction") var shopperInteraction = ""
    @SerializedName("country_code") var countryCode = "SE"
}