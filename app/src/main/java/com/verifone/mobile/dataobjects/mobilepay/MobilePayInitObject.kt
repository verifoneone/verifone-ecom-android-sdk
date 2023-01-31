package com.verifone.mobile.dataobjects.mobilepay

import com.google.gson.annotations.SerializedName
import com.verifone.connectors.threeds.dataobjects.ThreedsValidationData

class MobilePayInitObject {

    @SerializedName("payment_provider_contract")
    var paymentProviderContract = ""

    @SerializedName("redirect_url")
    var redirectUrl = "vfconnectors://"

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
    var dynamicDescriptor = "string1"

    @SerializedName("merchant_reference")
    var merchantReference = "nGZ2PEZZmqGPkym0DeqXRI1sA"

    @SerializedName("user_agent")
    var userAgent = "str"

    @SerializedName("currency_code")
    var currencyCode = "DKK"

    @SerializedName("sca_compliance_level")
    var scaComplianceLevel = "WALLET"

    @SerializedName("sca_exemption")
    var scaExemption = "1"

    @SerializedName("shipping_information")
    var shippingInformation = ShippingInformation()

}