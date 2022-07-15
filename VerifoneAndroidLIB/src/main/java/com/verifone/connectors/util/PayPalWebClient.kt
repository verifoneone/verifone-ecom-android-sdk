package com.verifone.connectors.util

import android.graphics.Bitmap

import android.webkit.WebView
import android.webkit.WebViewClient
import java.net.URI
import java.net.URL
import java.net.URLDecoder

class PayPalWebClient(onConfirmationDone: (MutableMap<String, String>) -> Unit): WebViewClient() {
    val sendConfirmationResult = onConfirmationDone
    var queryParamsMap:MutableMap<String,String> = LinkedHashMap()
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        val sourceURL = url.toString()
        if (sourceURL.isNotEmpty() && !isPaypalDomain(sourceURL)) {
            val tempURL = URL(sourceURL)
            queryParamsMap = parseQueryParams(tempURL)
            sendConfirmationResult(queryParamsMap)
            return
        }
    }

    private fun parseQueryParams(urlParam:URL):MutableMap<String, String> {
        val queryPairs: MutableMap<String, String> = LinkedHashMap()
        val query: String = urlParam.query
        val pairs = query.split("&").toTypedArray()
        for (pair in pairs) {
            val idx = pair.indexOf("=")
            queryPairs[URLDecoder.decode(pair.substring(0, idx), "UTF-8")] =
                URLDecoder.decode(pair.substring(idx + 1), "UTF-8")
        }
        return queryPairs
    }

    private fun isPaypalDomain(url: String?):Boolean {
        val uri = URI(url)
        val domain: String = uri.host
        return domain.contains("paypal.com")
    }

}