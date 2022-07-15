package com.verifone.mobile.dataobjects

import com.google.gson.annotations.SerializedName

class ReuseTokenRequest {
    @SerializedName("token_scope")
    var tokenScope = ""

    @SerializedName("encrypted_card")
    var encryptedCard = ""

    @SerializedName("public_key_alias")
    var publicKeyAlias = ""

    @SerializedName("token_type")
    var tokenType = ""

    //@SerializedName("token_expiry_date")
    //var tokenExpiryDate = ""
}