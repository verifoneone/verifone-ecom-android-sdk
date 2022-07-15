package com.verifone.mobile.responses

import com.google.gson.annotations.SerializedName

class ReuseTokenResponse {
    @SerializedName("reuse_token")
    var reuseToken:String = ""

    @SerializedName("bin")
    var bin= ""

    @SerializedName("expiry_month")
    var expiryMonth = 0

    @SerializedName("expiry_year")
    var expireYear = 0

    @SerializedName("last_four")
    var lastFour = "0"

    @SerializedName("updated_at")
    var updatedAt = ""

    @SerializedName("created_at")
    var createdAt = ""

    @SerializedName("token_expiry_date")
    var tokenExpiryDate = ""

    @SerializedName("token_scope")
    var tokenScope = ""

    @SerializedName("token_status")
    var tokenStatus = ""
}