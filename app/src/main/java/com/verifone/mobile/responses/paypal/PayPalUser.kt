package com.verifone.mobile.responses.paypal

import com.google.gson.annotations.SerializedName

class PayPalUser {
    @SerializedName("payerId")
    var payerID = ""

    @SerializedName("name")
    var name = PayPalUserName()

    @SerializedName("authorizationStatus") var authorizationStatus  = ""

}