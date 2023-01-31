package com.verifone.mobile.responses.vipps

import com.google.gson.annotations.SerializedName

class VippsAmount(transactionAmount:Int) {
    @SerializedName("amount")
    var amount = transactionAmount
}