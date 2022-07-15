package com.verifone.connectors.screens

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager

class PayPalConfirmationForm(ctx: Context, confirmationURL:String, onConfirmationComplete: (MutableMap<String, String>) -> Unit) {

    companion object{
        const val keyPaypalCancelUrl = "fullCancelUrl"
    }
    private val paypalFormTag = "paypal_screen_tag"
    private var mFragmentManager: FragmentManager

    private var payPalWebScreen:PaypalWebScreen =
        PaypalWebScreen(confirmationURL,onConfirmationComplete)

    init {
        ctx as AppCompatActivity
        mFragmentManager = ctx.supportFragmentManager
    }

    fun displayPaypalConfirmation() {
        payPalWebScreen.show(mFragmentManager,paypalFormTag)
    }

    fun dismissPaypalScreen() {
        payPalWebScreen.dismiss()
    }
}