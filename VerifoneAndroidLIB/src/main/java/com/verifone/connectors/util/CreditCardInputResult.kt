package com.verifone.connectors.util

import com.verifone.connectors.datapack.configuration.PayerCardData

class CreditCardInputResult {
    var encryptedCardData: String=""
    var payerName: String=""
    var cardProperties: PayerCardData = PayerCardData()
    var storeCard: Boolean = false
}