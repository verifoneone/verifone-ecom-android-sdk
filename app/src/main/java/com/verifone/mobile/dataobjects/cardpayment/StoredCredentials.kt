package com.verifone.mobile.dataobjects.cardpayment

import com.google.gson.annotations.SerializedName

class StoredCredentials {
    @SerializedName("stored_credential_type")
    val storedCredentialType = ""

    @SerializedName("processing_model")
    val processingModel =  ""

    @SerializedName("processing_model_details")
    val processingModelDetails = ProcessingModelDetails()

    @SerializedName("")
    val firstPayment = true
}