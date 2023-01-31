package com.verifone.mobile.dataobjects.paypal

import com.google.gson.annotations.SerializedName

class PayPalRequestObject(currencyParam:String,ppc:String,dynamicDescriptor:String,merchantReference:String,purchasedItems:ArrayList<PurchasedItem>) {

    @SerializedName("intent")
    val intentPayPal = "AUTHORIZE"
    @SerializedName("customer")
    val customer:CustomerInfo = CustomerInfo()
    @SerializedName("applicationContext")
    val appContext = AppContext()

    @SerializedName("shipping")
    val shippingData = ShippingData()

    @SerializedName("paymentProviderContract")
    val paymentProviderContract = ppc

    @SerializedName("items")
    var itemList:ArrayList<PurchasedItem> = purchasedItems

    @SerializedName("dynamicDescriptor")
    var dynamicDescriptor = dynamicDescriptor

    @SerializedName("merchantReference")
    var merchantReference = merchantReference

    @SerializedName("detailedAmount")
    var detailedAmountData = DetailedAmount(currencyParam,500)

    @SerializedName("amount")
    var amountData = UnitValueData("",0)


    init {
        var totalSum = 0
        var currencyCode = currencyParam
        for (it in itemList) {
            val tempQuantity = it.itemQuantity.toInt()
            currencyCode = it.unitAmount.currencyCode
            totalSum += it.unitAmount.itemValue * tempQuantity
            totalSum += it.unitTax.itemValue

        }
        totalSum -= detailedAmountData.discount.itemValue
        totalSum += detailedAmountData.handling.itemValue
        totalSum += detailedAmountData.shipping.itemValue
        totalSum += detailedAmountData.insurance.itemValue
        totalSum -= detailedAmountData.shippingDiscount.itemValue
        amountData = UnitValueData(currencyCode,totalSum)

        //val itm1 = PurchasedItem("Mac Laptop","1","Dell Laptop","123","PHYSICAL_GOODS")
        //val itm2 = PurchasedItem("Phone","10","Apple phone","456","PHYSICAL_GOODS")
        //itemList.add(itm1)
        //itemList.add(itm2)
    }
}