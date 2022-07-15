package com.verifone.connectors.screens


import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.verifone.connectors.datapack.configuration.FormUICustomizationData
import com.verifone.connectors.datapack.configuration.PaymentConfigurationData
import com.verifone.connectors.util.CreditCardInputResult

class VerifonePaymentForm(displayData:PaymentConfigurationData,onCardInputComplete: (cardInputResult:CreditCardInputResult) -> Unit) {
    companion object{
        val keyTitleTextColor = "titleTextColorValue"
        val keyFormBackground = "formBackgroundColor"
        val keyTextFieldBackground= "textFieldBackgroundColor"
        val keyInputTextColor = "inputTextColor"
        val keyHintColor = "hintTextColor"
        val keyPayButtonColor = "payButtonColor"
        val keyTextFontID = "textFontResourceID"
        val keyDisplayPrice = "displayPrice"
        val keyMerchantPublicKey = "merchantPublicEncryptionKey"
        val keyShowRecurrentPaymentsOption = "keyShowRecurrentOption"
        val keyCallbackCardInputResult = "keyCallbackCardInput"
        fun encryptCardData(expiryMonthParam: Int,
                               expiryYearParam: Int,
                               cardNumberParam: String,
                               cvvNumberParam: String,
                               publicKey: String):String {
            return CardFormScreen.encryptCardDetails(expiryMonthParam,expiryYearParam,cardNumberParam,cvvNumberParam,publicKey)
        }
    }

    private var mFragmentManager:FragmentManager
    private var cardInputScreen:CardFormScreen
    init {
        val ctx = displayData.activityContext
        ctx as AppCompatActivity
        cardInputScreen = CardFormScreen(ctx,displayData.displayCustomization,displayData.showStoredCardOption,displayData.displayPrice,displayData.payButtonText,displayData.merchantPublicKey,onCardInputComplete)
        mFragmentManager = ctx.supportFragmentManager
    }

    fun displayPaymentForm() {
        cardInputScreen.show(mFragmentManager,"card_form")
    }
}