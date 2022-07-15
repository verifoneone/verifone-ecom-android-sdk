package com.verifone.mobile.responses.paypal

import com.google.gson.annotations.SerializedName

class PayPalUserName {
    @SerializedName("firstName") var firstName = ""
    @SerializedName("lastName") var lastName = ""
}