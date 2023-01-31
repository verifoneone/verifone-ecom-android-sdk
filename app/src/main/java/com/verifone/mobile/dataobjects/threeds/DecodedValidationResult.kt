package com.verifone.mobile.dataobjects.threeds

import com.google.gson.annotations.SerializedName

class DecodedValidationResult {

    @SerializedName("authorization_payload")
    var authPayload = ""

    @SerializedName("cavv")
    var cavv = ""

    @SerializedName("cavv_algorithm")
    var cavvAlgorithm = ""

    @SerializedName("eci_flag")
    var eciFlag = ""

    @SerializedName("enrolled")
    var enrolled = ""

    @SerializedName("pares_status")
    var paresStatus = ""

    @SerializedName("reason_code")
    var reasonCode = ""

    @SerializedName("reason_desc")
    var reasonDesc = ""

    @SerializedName("signature_verification")
    var signatureVerification = ""

    @SerializedName("xid")
    var xid = ""
}