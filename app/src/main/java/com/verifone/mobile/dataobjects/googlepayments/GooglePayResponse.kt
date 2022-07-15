package com.verifone.mobile.dataobjects.googlepayments

import com.google.gson.annotations.SerializedName
import com.verifone.connectors.googlepay.AdditionalDataObject

class GooglePayResponse {
    @SerializedName("id")
    var id:String = ""

    @SerializedName("payment_provider_contract")
    var payment_provider_contract:String=""

    @SerializedName("amount")
    var amount:Int=0

    @SerializedName("blocked")
    var blocked:Boolean=false

    @SerializedName("merchant_reference")
    var merchant_reference:String=""

    @SerializedName("status")
    var status:String=""

    @SerializedName("authorization_code")
    var authorization_code:String=""

    @SerializedName("created_by")
    var created_by:String=""

    var cvv_present:Boolean = false
    var cvv_result:String=""
    var reason_code:String=""
    var rrn:String=""
    @SerializedName("shopper_interaction")
    var shopper_interaction:String=""
    var stan:String=""
    var reversal_status:String=""
    var country_code:String=""
    lateinit var additional_data: AdditionalDataObject

}