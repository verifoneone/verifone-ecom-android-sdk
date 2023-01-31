package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class PurchasedItem(paramItemName:String,paramItemQuantity:String,paramItemDescription:String
                    ,paramItemSKU:String,paramItemCategory:String,price:UnitValueData) {

    @SerializedName("name")
    var itemName = paramItemName

    @SerializedName("quantity")
    var itemQuantity = paramItemQuantity

    @SerializedName("description")
    var itemDescription = paramItemDescription

    @SerializedName("sku")
    var itemSKU = paramItemSKU

    @SerializedName("category")
    var itemCategory = paramItemCategory

    @SerializedName("unitAmount")
    var unitAmount = price

    @SerializedName("tax")
    var unitTax = UnitValueData(unitAmount.currencyCode,1000)
}