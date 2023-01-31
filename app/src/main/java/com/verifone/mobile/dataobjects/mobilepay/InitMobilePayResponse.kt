package com.verifone.mobile.dataobjects.mobilepay

import com.google.gson.annotations.SerializedName

class InitMobilePayResponse {
    @SerializedName("redirect_url")
    var redirectUrl = ""

    @SerializedName("created_by")
    var createdBy = ""

    @SerializedName("status")
    var status = ""

    @SerializedName("id")
    var id = ""
}