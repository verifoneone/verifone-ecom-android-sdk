package com.verifone.mobile.dataobjects

import com.google.gson.annotations.SerializedName

class RequestJwtObject {
    @SerializedName("threeds_contract_id")
    var threedsContractID:String = ""
    constructor(threedsContractParam:String) {
        threedsContractID = threedsContractParam
    }
}