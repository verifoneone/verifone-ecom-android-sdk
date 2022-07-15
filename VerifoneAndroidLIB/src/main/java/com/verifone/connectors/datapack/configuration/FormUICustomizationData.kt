package com.verifone.connectors.datapack.configuration

import android.graphics.Typeface
import android.text.style.BackgroundColorSpan

class FormUICustomizationData(
    formBackground: String = "",
    textFieldBackground: String = "",
    inputTextColor: String = "",
    hintColor: String = "",
    titleTextColor: String = "",
    buttonColor:String = "",
    textFont:Typeface?=null,
    textFontID:Int=0

) {
    var paymentFormBackground = formBackground
    var formTextFieldsBackground = textFieldBackground
    var formInputTextColor = inputTextColor
    var formTitleTextColor = titleTextColor
    var hintTextColor = hintColor
    var payButtonColor = buttonColor
    var userTextFont = textFont
    var userTextFontRes = textFontID
}