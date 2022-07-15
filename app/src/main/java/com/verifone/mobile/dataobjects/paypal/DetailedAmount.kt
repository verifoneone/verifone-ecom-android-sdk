package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class DetailedAmount(currencyCode:String,defValue:Int) {
    @SerializedName("discount")
    var discount = UnitValueData(currencyCode,defValue)

    @SerializedName("shippingDiscount")
    var shippingDiscount = UnitValueData(currencyCode,defValue)

    @SerializedName("insurance")
    var insurance = UnitValueData(currencyCode,defValue)

    @SerializedName("handling")
    var handling = UnitValueData(currencyCode,defValue)

    @SerializedName("shipping")
    var shipping = UnitValueData(currencyCode,defValue)
}