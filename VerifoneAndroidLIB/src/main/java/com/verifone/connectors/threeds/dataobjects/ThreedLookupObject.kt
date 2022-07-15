package com.verifone.connectors.threeds.dataobjects

import com.google.gson.annotations.SerializedName

class ThreedLookupObject {

    @SerializedName("amount")
    var amount = 1

    @SerializedName("billing_first_name")
    var billing_first_name = ""

    @SerializedName("billing_last_name")
    var billing_last_name = ""

    @SerializedName("billing_phone")
    var billing_phone = ""

    @SerializedName("billing_address_1")
    var billing_address_1 = ""


    @SerializedName("billing_city")
    var billing_city = ""

    @SerializedName("billing_postal_code")
    var billing_postal_code = ""

    @SerializedName("billing_state")
    var billing_state = ""

    @SerializedName("billing_country_code")
    var billing_country_code = "US"

    @SerializedName("encrypted_card")
    var card = ""

    @SerializedName("public_key_alias")
    var publicKeyAlias = ""

    @SerializedName("currency_code")
    var currency_code = ""

    @SerializedName("device_info_id")
    var device_info_id = ""

    @SerializedName("email")
    var email = ""

    @SerializedName("merchant_reference")
    var merchant_reference = ""

    @SerializedName("threeds_contract_id")
    var threeds_contract_id  = ""

    @SerializedName("token")
    var token = ""

}