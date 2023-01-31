package com.verifone.mobile.dataobjects.klarna

import com.google.gson.annotations.SerializedName

class LocaleObject {
    @SerializedName("country_code") var countryCode = "SE"
    @SerializedName("language") var language = "en"
}