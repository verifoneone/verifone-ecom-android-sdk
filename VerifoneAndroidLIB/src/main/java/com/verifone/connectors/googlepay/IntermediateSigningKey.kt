package com.verifone.connectors.googlepay

import com.google.gson.annotations.SerializedName

class IntermediateSigningKey {
    constructor(signed_key:String, signatures_array:Array<String>) {
        literals = signatures_array
        signedKey = signed_key
    }

    @SerializedName("signedKey")
    private var signedKey:String=""

    @SerializedName("signatures")
    private var literals = arrayOf("")

}