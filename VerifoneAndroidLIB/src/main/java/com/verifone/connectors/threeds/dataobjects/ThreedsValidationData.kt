package com.verifone.connectors.threeds.dataobjects

import com.google.gson.annotations.SerializedName

class ThreedsValidationData(eciFlag: String?, enrolledParam: String?, cavvParam: String?,
                            paresStatus: String?, signatureVerification: String?, dsTransactionID: String?, xidParam:String?
)
{
    constructor() : this("","","","","","","") {

    }

    var failReason = ""

    @SerializedName("eci_flag")
    var eciFlag = eciFlag

    @SerializedName("enrolled")
    var enrolled = if (enrolledParam?.isEmpty() == true) "Y" else enrolledParam

    @SerializedName("cavv")
    var cavv = cavvParam

    @SerializedName("pares_status")
    var paresStatus = paresStatus


    @SerializedName("xid")
    var xid = xidParam

    @SerializedName("ds_transaction_id")
    var dsTransactionId = dsTransactionID

    @SerializedName("signature_verification")
    var signatureVerification = signatureVerification

    @SerializedName("error_desc")
    var errorDesc = ""

    @SerializedName("error_no")
    var errorNo = ""

}