package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class CustomerInfo {
    @SerializedName("email")
    var email = "paypalcustomer@domain.com"

    @SerializedName("payerId")
    var payerID = "WDJJHEBZ4X2LY"

    @SerializedName("phoneNumber")
    var phone:PhoneData = PhoneData()

    @SerializedName("birthDate")
    var dateOfBirth = "2000-01-31"

    @SerializedName("identification")
    var identification = Identification()

    @SerializedName("address")
    var address:PayerAddress = PayerAddress("US","570023", "IN-MH","yyy","add1","add2")

    @SerializedName("firstName")
    var firstName = "James"
    @SerializedName("lastName")
    var lastName = "Smith"
}