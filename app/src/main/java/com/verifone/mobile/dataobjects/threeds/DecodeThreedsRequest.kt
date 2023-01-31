package com.verifone.mobile.dataobjects.threeds

import com.google.gson.annotations.SerializedName

class DecodeThreedsRequest {
    @SerializedName("authentication_id")
    var authID = ""
    @SerializedName("jwt")
    var jwt = ""
    @SerializedName("threeds_contract_id")
    var threedsContractID = ""
}