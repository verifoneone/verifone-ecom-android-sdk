package com.verifone.connectors.screens

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.verifone.connectors.R


internal class PaymentOptionsDialog(displayOptionsParam: ArrayList<String>,onPayMethodSelected:(payMethod:String) -> Unit,ctx: Context): Dialog(ctx) {
    private lateinit var mCardOptionBtn:ConstraintLayout
    private lateinit var mPaypalOptionBtn:ConstraintLayout
    private lateinit var mGooglePayOptionBtn:ConstraintLayout
    private lateinit var mCloseBtn:AppCompatImageView
    //var startCardFlow = startCardFlowParam
    //var startPayPalFlow = startPayPalFlowParam
    val payMethodSelected = onPayMethodSelected
    val paymentOptionButtons = ArrayList<ConstraintLayout>(2)
    val paymentOptionImages = ArrayList<AppCompatImageView>(2)
    val paymentOptionTexts = ArrayList<AppCompatTextView>(2)
    val selectedPaymentOptions = displayOptionsParam
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.payment_options_layout)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        paymentOptionButtons.add(findViewById(R.id.pay_option_container1))
        paymentOptionButtons.add(findViewById(R.id.pay_option_container2))
        paymentOptionButtons.add(findViewById(R.id.pay_option_container3))
        paymentOptionImages.add(findViewById(R.id.pay_option_image1))
        paymentOptionImages.add(findViewById(R.id.pay_option_image2))
        paymentOptionImages.add(findViewById(R.id.pay_option_image3))
        paymentOptionTexts.add(findViewById(R.id.pay_option_text1))
        paymentOptionTexts.add(findViewById(R.id.pay_option_text2))
        paymentOptionTexts.add(findViewById(R.id.pay_option_text3))

        mCloseBtn = findViewById(R.id.close_btn)
        mCloseBtn.setOnClickListener {
            dismiss()
        }

        setupSelectedPayOptions(selectedPaymentOptions)

    }
    private fun setupSelectedPayOptions(displayOptions: ArrayList<String>) {
        for ((i, index) in displayOptions.withIndex()) {
            if (index == VerifonePaymentOptions.paymentOptionCard) {
                mCardOptionBtn = paymentOptionButtons[i]
                mCardOptionBtn.visibility = View.VISIBLE
                mCardOptionBtn.setOnClickListener {
                    //startCardFlow(true)
                    payMethodSelected(VerifonePaymentOptions.paymentOptionCard)
                    dismiss()
                }
                paymentOptionTexts[i].setText(R.string.paymentProductTitleCard)
                paymentOptionImages[i].setImageResource(R.drawable.card_symbol)
            } else if (index == VerifonePaymentOptions.paymentOptionPayPal) {
                mPaypalOptionBtn = paymentOptionButtons[i]
                mPaypalOptionBtn.visibility = View.VISIBLE
                mPaypalOptionBtn.setOnClickListener {
                    //startPayPalFlow()
                    payMethodSelected(VerifonePaymentOptions.paymentOptionPayPal)
                    dismiss()
                }
                paymentOptionTexts[i].setText(R.string.paymentProductTitlePaypal)
                paymentOptionImages[i].setImageResource(R.drawable.paypal_logo)
            } else if (index == VerifonePaymentOptions.paymentOptionGooglePay){
                mGooglePayOptionBtn = paymentOptionButtons[i]
                mGooglePayOptionBtn.visibility = View.VISIBLE
                mGooglePayOptionBtn.setOnClickListener {
                    //startPayPalFlow()
                    payMethodSelected(VerifonePaymentOptions.paymentOptionGooglePay)
                    dismiss()
                }
                paymentOptionTexts[i].setText(R.string.paymentProductTitleGooglePay)
                paymentOptionImages[i].setImageResource(R.drawable.sv_google_pay_logo)
                //paymentOptionImages[i].backgroundTintList = ColorStateList()
            }
        }
    }

}