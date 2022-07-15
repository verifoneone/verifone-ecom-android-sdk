package com.verifone.mobile.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.verifone.mobile.R

class PaymentFlowDone: AppCompatActivity() {
    companion object {
        const val keyTransactionAmount = "transaction_amount_value"
        const val keyTransactionCurrency = "transaction_amount_currency"
        const val keyTransactionReference = "transaction_reference_value"
        const val keyPayerName = "payer_name"
        const val keyIsPayPal = "is_paypal"
        const val keyIsGooglePay = "is_google"
    }
    private lateinit var mPayerNameTV:AppCompatTextView
    private lateinit var mTransactionReferenceTV:AppCompatTextView
    private lateinit var mTransactionAmountTV:AppCompatTextView
    private lateinit var mTransactionAmountTopTV:AppCompatTextView
    private lateinit var mGoBackBtn:AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_done_layout)
        mPayerNameTV = findViewById(R.id.customer_value_tv)
        mTransactionReferenceTV = findViewById(R.id.reference_value_tv)
        mTransactionAmountTV = findViewById(R.id.amount_value_tv)
        mTransactionAmountTopTV = findViewById(R.id.transaction_value_top)
        mGoBackBtn = findViewById(R.id.back_to_store)
        mGoBackBtn.setOnClickListener {
            finish()
        }

        mPayerNameTV.text = intent.getStringExtra(keyPayerName)
        val isPaypal = intent.getBooleanExtra(keyIsPayPal,false)
        val amountStr = intent.getStringExtra(keyTransactionAmount)
        val amountVal = amountStr?.toInt()?:0
        val currency = intent.getStringExtra(keyTransactionCurrency)

        val displayVal = amountVal/100


        val fullDisplayVal = "$displayVal $currency"
        mTransactionAmountTV.text = fullDisplayVal
        mTransactionReferenceTV.text = intent.getStringExtra(keyTransactionReference)
        mTransactionAmountTopTV.text = fullDisplayVal
    }
}