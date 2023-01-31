package com.verifone.mobile.responses.vipps

import com.google.gson.annotations.SerializedName

class VippsShippingInfo {
    @SerializedName("address")
    var address = "3732  Metz Lane"

    @SerializedName("city")
    var city = "West Roxbury"

    @SerializedName("country")
    var country = "US"

    @SerializedName("postal_code")
    var postalCode = "1114"

    @SerializedName("email")
    var email = "user@example.com"

    @SerializedName("first_name")
    var firstName = "Thelma"

    @SerializedName("last_name")
    var lastName = "Tatro"

    @SerializedName("phone")
    var phone = 8577532706

    @SerializedName("state")
    var state = "MA"
}