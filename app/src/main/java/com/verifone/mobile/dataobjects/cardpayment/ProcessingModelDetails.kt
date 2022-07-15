package com.verifone.mobile.dataobjects.cardpayment

import com.google.gson.annotations.SerializedName

class ProcessingModelDetails {
    @SerializedName("total_payment_number")
    val totalPaymentNumber = 0

    @SerializedName("total_payment_amount")
    val totalPaymentAmount = 0

    @SerializedName("first_payment_amount")
    val firstPaymentAmount = 0

    @SerializedName("payment_frequency")
    val paymentFrequency = PaymentFrequency()

    @SerializedName("processing_model")
    val processingModel = ""
}