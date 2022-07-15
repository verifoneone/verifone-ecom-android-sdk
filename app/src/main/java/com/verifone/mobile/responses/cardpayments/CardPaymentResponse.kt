package com.verifone.mobile.responses.cardpayments

import com.google.gson.annotations.SerializedName

class CardPaymentResponse {

    var api_fail_message = ""
    var api_fail_cause = ""

    @SerializedName("id")
    var id = ""

    @SerializedName("payment_provider_contract")
    var paymentProviderContract = ""

    @SerializedName("amount")
    var amount = 0

    @SerializedName("blocked")
    var blocked = true

    @SerializedName("created_at")
    var createdAt = ""

    @SerializedName("customer")
    var customer = ""

    @SerializedName("merchant_reference")
    var merchantReference = ""

    @SerializedName("payment_product")
    var paymentProduct = "string"

    @SerializedName("payment_product_type")
    var paymentProductType = ""

    @SerializedName("processor_reference")
    var processorReference = ""

    @SerializedName("status")
    var status = ""

    @SerializedName("status_reason")
    var statusReason = "string"

    @SerializedName("arn")
    var arn = ""

    @SerializedName("authorization_code")
    var authorizationCode = ""

    @SerializedName("scheme_reference")
    var schemeReference = ""

    @SerializedName("avs_result")
    var avsResult = ""

    @SerializedName("card")
    var card = ""

    @SerializedName("created_by")
    var createdBy = ""

    @SerializedName("cvv_present")
    var cvvPresent = true

    @SerializedName("cvv_result")
    var cvvResult = ""

    @SerializedName("cavv_result")
    var cavvResult = ""

    //@SerializedName("stored_credential")
    //var storedCredential = {}

    @SerializedName("details")
    var details = DetailsDataObject()

    @SerializedName("reason_code")
    var reasonCode = ""

    @SerializedName("rrn")
    var rrn = ""

    @SerializedName("shopper_interaction")
    var shopperInteraction = ""

    @SerializedName("stan")
    var stan = ""


    @SerializedName("reversal_status")
    var reversalStatus = ""

    @SerializedName("city")
    var city = ""

    @SerializedName("country_code")
    var countryCode = ""


    @SerializedName("additional_data")
    var additionalData = AdditionalDataObject()
}