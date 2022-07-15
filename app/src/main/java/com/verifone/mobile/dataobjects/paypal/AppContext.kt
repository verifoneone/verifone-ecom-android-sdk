package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class AppContext {
    @SerializedName("brandName")
    var brandName = "MAHENDRA"
    @SerializedName("shippingPreference")
    var customerProvided = "CustomerProvided"
    @SerializedName("returnUrl")
    var returnURL = "https://68f8497efb9ce8aeef3ed419c6ef0597.m.pipedream.net/success"
    @SerializedName("cancelUrl")
    var cancelURL = "https://68f8497efb9ce8aeef3ed419c6ef0597.m.pipedream.net/cancel"
}