package com.verifone.mobile.dataobjects.klarna

import com.google.gson.annotations.SerializedName

class KlarnaPurchasedItem {
    @SerializedName("image_url") var imageURL = "https://static.footshop.com/661993/190909.jpg"
    @SerializedName("name") var name = "string"
    @SerializedName("quantity") var quantity = 1
    @SerializedName("unit_price")  var unitPrice = 1000
    @SerializedName("tax_rate") var taxRate = 0
    @SerializedName("discount_amount") var discountAmount = 0
    @SerializedName("total_tax_amount") var totalTaxRate = 0
    @SerializedName("total_amount") var  totalAmount = 1000
    @SerializedName("sku") var sku = "string"
    @SerializedName("description") var description = "string"
    @SerializedName("category") var category = "DIGITAL_GOODS"
    @SerializedName("reference") var itemReference = "AccessoryBag-Ref-ID-0001"
}