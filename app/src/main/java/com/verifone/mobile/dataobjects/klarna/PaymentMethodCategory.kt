package com.verifone.mobile.dataobjects.klarna

import com.google.gson.annotations.SerializedName

class PaymentMethodCategory {
    @SerializedName("identifier") var categoryIdentifier = ""
    @SerializedName("name") var categoryName = ""
    @SerializedName("asset_urls") var assetUrls = AssetUrls()
}