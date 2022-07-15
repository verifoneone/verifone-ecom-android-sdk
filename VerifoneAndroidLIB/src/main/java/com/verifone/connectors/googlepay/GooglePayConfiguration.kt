package com.verifone.connectors.googlepay

import org.json.JSONArray

class GooglePayConfiguration {
    var gatewayMerchantID=""
    var transactionAmount:Double= 0.0
    var shippingCost:Long=0
    var countryCode:String = "US"
    var currencyCode:String="USD"
    var priceStatus = "FINAL"
    var phoneNumberRequired = false
    var supportedCountries = JSONArray()
    var merchantName = ""
    var merchantID = ""

}