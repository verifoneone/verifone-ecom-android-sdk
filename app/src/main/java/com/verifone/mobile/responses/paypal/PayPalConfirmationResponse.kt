package com.verifone.mobile.responses.paypal

import com.google.gson.annotations.SerializedName

class PayPalConfirmationResponse {

    var isError:Boolean = false

    var errorMessage = ""

    @SerializedName("authorizationId")
    var authorizationID = ""

    @SerializedName("createdAt")
    var createdAT =  ""

    @SerializedName("expiresAt") var expiresAT = ""

    @SerializedName("status") var status = ""

    @SerializedName("instoreReference") var instoreReference = ""

    @SerializedName("payer") var payer = PayPalUser()

}