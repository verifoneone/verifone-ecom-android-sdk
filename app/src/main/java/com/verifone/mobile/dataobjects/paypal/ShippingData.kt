package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class ShippingData {
    @SerializedName("address")
    var address:PayerAddress = PayerAddress("IN","91", "IN-MH","yyy","add1","add2")

    @SerializedName("fullName")
    var fullName = "JamesSmith"
}