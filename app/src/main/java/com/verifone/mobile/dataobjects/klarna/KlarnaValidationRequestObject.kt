package com.verifone.mobile.dataobjects.klarna

import com.google.gson.annotations.SerializedName

class KlarnaValidationRequestObject(tokenParam:String) {
    @SerializedName("authorization_token") var authToken = tokenParam
}