package com.verifone.mobile.responses

import com.google.gson.annotations.SerializedName

class TestLookupResponse {
    @SerializedName("acs_rendering_type")
    var acs_rendering_type = ""

    @SerializedName("acs_transaction_id")
    var acs_transaction_id = ""

    @SerializedName("acs_url")
    var acs_url = ""

    @SerializedName("authentication_id")
    var authentication_id = ""

    @SerializedName("authentication_type")
    var authentication_type = ""

    @SerializedName("authorization_payload")
    var authorization_payload  =""

    @SerializedName("cardholder_info")
    var cardholder_info = ""

    @SerializedName("cavv")
    var cavv = ""

    @SerializedName("cavv_algorithm")
    var cavv_algorithm = ""

    @SerializedName("challenge_required")
    var challenge_required = ""

    @SerializedName("card_brand")
    var card_brand = ""

    @SerializedName("ds_transaction_id")
    var ds_transaction_id = ""

    @SerializedName("eci_flag")
    var eci_flag = ""

    @SerializedName("enrolled")
    var enrolled = ""

    @SerializedName("error_desc")
    var error_desc = ""

    @SerializedName("error_no")
    var error_no = ""

    @SerializedName("network_score")
    var network_score = ""

    @SerializedName("order_id")
    var order_id = ""

    @SerializedName("pares_status")
    var pares_status = ""

    @SerializedName("payload")
    var payload = ""

    @SerializedName("reason_code")
    var reason_code = ""

    @SerializedName("reason_desc")
    var reason_desc = ""

    @SerializedName("signature_verification")
    var signature_verification = ""

    @SerializedName("status_reason")
    var status_reason = ""

    @SerializedName("third_party_token")
    var third_party_token = ""

    @SerializedName("threeds_version")
    var threeds_version = ""

    @SerializedName("token")
    var token = ""

    @SerializedName("transaction_id")
    var transaction_id = ""

    @SerializedName("xid")
    var xid = ""
}