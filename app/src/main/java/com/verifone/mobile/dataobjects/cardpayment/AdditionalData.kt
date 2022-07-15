package com.verifone.mobile.dataobjects.cardpayment

import com.google.gson.annotations.SerializedName

class AdditionalData {
    @SerializedName("total_items")
    private var totalItems = 0
    @SerializedName("device_channel")
    private var deviceChannel = ""
    @SerializedName("status_reason")
    private var statusReason = ""
    @SerializedName("challenge_indicator")
    private var challengeIndicator = ""
    @SerializedName("challenge_cancel")
    private var challengeCancel = ""
    @SerializedName("acs_url")
    private var acsUrl = ""
    @SerializedName("acs_operator_id")
    private var acsOperatorID = ""
    @SerializedName("network_score")
    private var networkScore = ""
    @SerializedName("reason_code")
    private var reasonCode = ""
    @SerializedName("reason_desc")
    private var reasonDesc = ""
}