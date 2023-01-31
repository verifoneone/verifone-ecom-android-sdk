package com.verifone.mobile.dataobjects.threeds

import com.google.gson.annotations.SerializedName

class DecodedThreedsData {
    @SerializedName("authentication_id")
    var authenticationID = ""

    @SerializedName("action_code")
    var actionCode = ""

    @SerializedName("error_no")
    var errorNO = ""

    @SerializedName("error_desc")
    var errorDesc = ""

    @SerializedName("validation_result")
    var validationResult = DecodedValidationResult()

}