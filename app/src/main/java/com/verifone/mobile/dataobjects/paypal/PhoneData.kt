package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class PhoneData {
    @SerializedName("phoneType")
    var phoneType = "MOBILE"
    @SerializedName("value")
    var nrValue = "64646464"
}