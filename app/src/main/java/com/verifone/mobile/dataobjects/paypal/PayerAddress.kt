package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class PayerAddress(paramCountry:String,paramPostalCode:String,paramSubDivision:String,paramCity:String
                   ,paramAddressLine1:String,paramAddressLine2:String) {

    @SerializedName("country")
    var country =  paramCountry

    @SerializedName("postalCode")
    var postalCode = paramPostalCode

    @SerializedName("countrySubdivision")
    var subDivision = paramSubDivision

    @SerializedName("city")
    var city = paramCity

    @SerializedName("addressLine1")
    var addressLine1 = paramAddressLine1

    @SerializedName("addressLine2")
    var addressLine2 = paramAddressLine2

}