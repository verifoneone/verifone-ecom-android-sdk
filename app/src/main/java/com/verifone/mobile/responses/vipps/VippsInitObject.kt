package com.verifone.mobile.responses.vipps

import com.google.gson.annotations.SerializedName

class VippsInitObject {
    @SerializedName("payment_provider_contract")
    var paymentProviderContract = ""

    @SerializedName("redirect_url")
    var redirectUrl = "vippsmt://"

    @SerializedName("is_app")
    var isApp = true

    @SerializedName("amount")
    var amount = 1000

    @SerializedName("auth_type")
    var authType = "FINAL_AUTH"

    @SerializedName("capture_now")
    var captureNow = false


    @SerializedName("customer")
    var customer = ""

    @SerializedName("customer_ip")
    var customerIP = "127.0.0.1"

    @SerializedName("dynamic_descriptor")
    var dynamicDescriptor = "Test merchant"

    @SerializedName("merchant_reference")
    var merchantReference = "5690"

    @SerializedName("user_agent")
    var userAgent = "str"

    @SerializedName("currency_code")
    var currencyCode = "NOK"

    @SerializedName("sca_compliance_level")
    var scaComplianceLevel = "WALLET"

    @SerializedName("sca_exemption")
    var scaExemption = "1"
}