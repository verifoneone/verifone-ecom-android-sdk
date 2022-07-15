package com.verifone.mobile.responses.paypal

import com.google.gson.annotations.SerializedName

class PayPalResponse {
    @SerializedName("id")
    var id = ""
    @SerializedName("status")
    var status = ""
    @SerializedName("orderId")
    var orderID = ""
    @SerializedName("createdAt")
    var createdAT = ""
    @SerializedName("instoreReference")
    var inStoreRef = ""
    @SerializedName("approvalUrl")
    var approvalURL = ""
}