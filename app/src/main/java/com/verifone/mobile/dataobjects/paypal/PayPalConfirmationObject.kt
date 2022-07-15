package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName
import com.verifone.mobile.screens.CustomizationSettings

class PayPalConfirmationObject(merchantRef:String) {
    @SerializedName("merchantReference")
    var merchantReference = merchantRef
}