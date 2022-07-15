package com.verifone.mobile.responses

import com.google.gson.annotations.SerializedName

class JWTResponse {

    var api_fail_message = ""
    var api_fail_cause = ""

    @SerializedName("jwt")
    var jwt:String = ""

    @SerializedName("threeds_contract_id")
    var threedsContractID:String = ""
}