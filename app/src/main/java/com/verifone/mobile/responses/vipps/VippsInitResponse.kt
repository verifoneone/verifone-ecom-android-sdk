package com.verifone.mobile.responses.vipps

import com.google.gson.annotations.SerializedName

class VippsInitResponse {
    @SerializedName("amount")
    var amount = 0
    @SerializedName("created_at")
    var createdAT = ""
    @SerializedName("customer")
    var customer = ""
    @SerializedName("status")
    var status = ""
    @SerializedName("shopper_interaction")
    var shopperInteraction = ""
    @SerializedName("country_code")
    var countryCode = ""
    @SerializedName("id")
    var id = ""
    @SerializedName("redirect_url")
    var redirectURL = ""
    @SerializedName("processor")
    var processor = "NETS"
    @SerializedName("payment_product")
    var paymentProduct = ""

}