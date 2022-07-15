package com.verifone.connectors.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView

import androidx.fragment.app.DialogFragment
import com.verifone.connectors.R
import com.verifone.connectors.util.PayPalWebClient

internal class PaypalWebScreen(displayURL: String,onConfirmationComplete: (MutableMap<String, String>) -> Unit):DialogFragment() {

    private lateinit var payPalWebView: WebView
    private val urlToLoad = displayURL
    private val confirmationComplete = onConfirmationComplete
    private lateinit var mPaypalWebClient:PayPalWebClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        payPalWebView.loadUrl(urlToLoad)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val vv = inflater.inflate(R.layout.paypal_screen_layout,null)
        payPalWebView = vv.findViewById(R.id.paypal_web_view)
        mPaypalWebClient = PayPalWebClient(confirmationComplete)
        payPalWebView.webViewClient = mPaypalWebClient
        payPalWebView.settings.javaScriptEnabled = true
        return vv
    }

    override fun onStop() {
        super.onStop()
        if (mPaypalWebClient.queryParamsMap.isEmpty()) {
            confirmationComplete(mPaypalWebClient.queryParamsMap)
        }
    }

}