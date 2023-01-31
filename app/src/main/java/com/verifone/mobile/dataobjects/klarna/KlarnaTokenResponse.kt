package com.verifone.mobile.dataobjects.klarna

import com.google.gson.annotations.SerializedName

class KlarnaTokenResponse {

    @SerializedName("id")
    var id = ""

    @SerializedName("instore_reference")
    var inStoreReference = ""

    @SerializedName("client_token")
    var clientToken = ""

    @SerializedName("payment_method_categories")
    var paymentMethodCategories = arrayOfNulls<PaymentMethodCategory>(0)

    @SerializedName("amount") var amount = 0

    @SerializedName("blocked") var blocked = false

    @SerializedName("customer") var customer =""

    @SerializedName("details") var details = DetailsObject()

    @SerializedName("merchant_reference") var merchantReference = ""

    @SerializedName("processor") var processor = ""

    @SerializedName("payment_product") var paymentProduct = ""

    @SerializedName("payment_product_type") var paymentProductType = ""

    @SerializedName("status") var status = ""

    @SerializedName("created_by") var createdBy = ""

    @SerializedName("shopper_interaction") var shopperInteraction =  ""

    @SerializedName("geo_location") var geoLocation = floatArrayOf(0f)

    @SerializedName("country_code") var countryCode = ""

}