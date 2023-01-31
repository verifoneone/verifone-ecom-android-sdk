package com.verifone.connectors.klarna

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentView
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentViewCallback
import com.klarna.mobile.sdk.api.payments.KlarnaPaymentsSDKError
import com.verifone.connectors.R
import com.verifone.connectors.klarna.KlarnaPaymentForm.Companion.keyAuthToken
import com.verifone.connectors.klarna.KlarnaPaymentForm.Companion.keyClientToken
import com.verifone.connectors.klarna.KlarnaPaymentForm.Companion.keyPaymentCategory
import com.verifone.connectors.klarna.KlarnaPaymentForm.Companion.keyReturnURL
import com.verifone.connectors.klarna.KlarnaPaymentForm.Companion.klarnaAuthorizationFail
import com.verifone.connectors.klarna.KlarnaPaymentForm.Companion.klarnaAuthorizationSuccess


internal class KlarnaAuthorizationScreen: AppCompatActivity(), KlarnaPaymentViewCallback {

    var authorizationToken:String = ""
    private var clientToken =""
    private var returnURL =""
    private val authorizeButton by lazy { findViewById<Button>(R.id.authorizeButton) }
    private val finalizeButton by lazy { findViewById<Button>(R.id.finalizeButton) }
    private lateinit var klarnaPaymentView: KlarnaPaymentView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.klarna_screen_layout)
        clientToken = intent.getStringExtra(keyClientToken).toString()
        returnURL = intent.getStringExtra(keyReturnURL).toString()
        klarnaPaymentView = findViewById(R.id.klarna_payments)
        klarnaPaymentView.category = intent.getStringExtra(keyPaymentCategory)
        klarnaPaymentView.registerPaymentViewCallback(this)
        klarnaPaymentView.initialize(clientToken, returnURL)
        setupButtons()
    }

    private fun setupButtons() {
        authorizeButton.setOnClickListener {
            klarnaPaymentView.authorize(true, null)
        }

        finalizeButton.setOnClickListener {
            klarnaPaymentView.finalize(null)
        }
    }

    override fun onAuthorized(
        view: KlarnaPaymentView,
        approved: Boolean,
        authToken: String?,
        finalizedRequired: Boolean?
    ) {
        if (!authToken.isNullOrEmpty()) {
            authorizationToken = authToken
        }

        finalizeButton.isEnabled = finalizedRequired ?: false
        if (!finalizeButton.isEnabled){
            val tokenData = Intent()
            tokenData.putExtra(keyAuthToken,authorizationToken)
            setResult(klarnaAuthorizationSuccess,tokenData)
            finish()
        }
    }

    override fun onErrorOccurred(view: KlarnaPaymentView, error: KlarnaPaymentsSDKError) {
        setResult(klarnaAuthorizationFail)
        finish()
    }

    override fun onFinalized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {
        if (!authToken.isNullOrEmpty()) {
            authorizationToken = authToken
        }
    }

    override fun onInitialized(view: KlarnaPaymentView) {
        view.load(null)
    }

    override fun onLoadPaymentReview(view: KlarnaPaymentView, showForm: Boolean) {

    }

    override fun onLoaded(view: KlarnaPaymentView) {
        authorizeButton.isEnabled = true
    }

    override fun onReauthorized(view: KlarnaPaymentView, approved: Boolean, authToken: String?) {

    }
}