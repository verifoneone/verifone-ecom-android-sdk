package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class UnitValueData {
    constructor()
    constructor(paramCurrency:String="",paramValue:Int=0){
        if (paramCurrency.isNotEmpty() && paramValue>=0){
            currencyCode = paramCurrency
            itemValue = paramValue
        }
    }



    @SerializedName("currencyCode")
    var currencyCode = ""
    @SerializedName("value")
    var itemValue = 1000
}