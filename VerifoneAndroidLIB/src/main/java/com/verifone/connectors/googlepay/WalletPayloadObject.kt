package com.verifone.connectors.googlepay

import com.google.gson.annotations.SerializedName

class WalletPayloadObject {
    constructor(signature_param:String,protocol_version:String,signed_message:String,intermediate_sign_key:String,intermediate_signatures:Array<String>) {
        signature = signature_param
        protocolVersion = protocol_version
        signedMessage = signed_message
        intermediateSigningKey = IntermediateSigningKey(intermediate_sign_key,intermediate_signatures)
    }
    constructor()

    @SerializedName("signature")
    var signature = ""

    @SerializedName("intermediateSigningKey")
    private var intermediateSigningKey:IntermediateSigningKey = IntermediateSigningKey("", arrayOf(""))

    @SerializedName("protocolVersion")
    private var protocolVersion = ""

    @SerializedName("signedMessage")
    private var signedMessage =""

}