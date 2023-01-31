package com.verifone.mobile.screens

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import com.verifone.mobile.R
import java.lang.Exception

class PaymentFlowDone: AppCompatActivity() {
    enum class TransactionType {
        typeCreditCard,
        typeKlarna,
        typeSwish,
        typeGooglePay,
        typeMobilePay,
        typeVipps,
        typePayPal
    }
    companion object {
        const val keyTransactionAmount = "transaction_amount_value"
        const val keyTransactionCurrency = "transaction_amount_currency"
        const val keyTransactionReference = "transaction_reference_value"
        const val keyTransactionType = "transaction_type"
        const val keyPayerName = "payer_name"
    }
    private lateinit var mPayerNameTV:AppCompatTextView
    private lateinit var mTransactionReferenceTV:AppCompatTextView
    private lateinit var mTransactionAmountTV:AppCompatTextView
    private lateinit var mTransactionAmountTopTV:AppCompatTextView
    private lateinit var mGoBackBtn:AppCompatButton
    private var mTransactionType =""

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

        mTransactionType = intent.getStringExtra(keyTransactionType)?:""
        val amountStr = intent.getStringExtra(keyTransactionAmount)
        val currency = intent.getStringExtra(keyTransactionCurrency)

        var fullDisplayVal = "$amountStr $currency"

        if (mTransactionType == TransactionType.typeKlarna.name
            || mTransactionType==TransactionType.typeCreditCard.name
            || mTransactionType==TransactionType.typeGooglePay.name
            || mTransactionType==TransactionType.typePayPal.name) {
            var amountTemp = amountStr?.toInt()?:0
            amountTemp /= 100
            fullDisplayVal = "$amountTemp $currency"
        }


        mTransactionAmountTV.text = fullDisplayVal
        mTransactionReferenceTV.text = intent.getStringExtra(keyTransactionReference)
        mTransactionAmountTopTV.text = fullDisplayVal
    }
}