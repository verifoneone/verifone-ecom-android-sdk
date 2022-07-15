package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class Identification {
    @SerializedName("taxIdentificationNumber")
    var taxIdentificationNumber = "123456"

    @SerializedName("taxIdentificationType")
    var taxIdentificationType = "BR_CNPJ"
}