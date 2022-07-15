/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.verifone.connectors

import android.app.Activity
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.Wallet
import com.verifone.connectors.googlepay.GooglePayConfiguration

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.roundToLong

/**
 * Contains helper static methods for dealing with the Payments API.
 *
 * Many of the parameters used in the code are optional and are set here merely to call out their
 * existence. Please consult the documentation to learn more and feel free to remove ones not
 * relevant to your implementation.
 */
internal class GooglePayUtils {
    companion object {

        private val allowedCardNetworks = JSONArray(Constants.SUPPORTED_NETWORKS)
        private val allowedCardAuthMethods = JSONArray(Constants.SUPPORTED_METHODS)

        private val merchantInfo = JSONObject()
        private val baseRequest = JSONObject().apply {
            put("apiVersion", 2)
            put("apiVersionMinor", 0)
        }

        val MICROS = BigDecimal(1000000.0)

        private fun gatewayTokenizationSpecification(gatewayParam:String, merchantID:String): JSONObject {
            return JSONObject().apply {
                put("type", "PAYMENT_GATEWAY")
                put("parameters", JSONObject(mapOf(
                    "gateway" to gatewayParam,
                    "gatewayMerchantId" to merchantID)))
            }
        }
        private fun cardPaymentMethod(gatewayParam:String,merchantID:String): JSONObject {
            val cardPaymentMethod = baseCardPaymentMethod()
            cardPaymentMethod.put("tokenizationSpecification", gatewayTokenizationSpecification(gatewayParam,merchantID))

            return cardPaymentMethod
        }

        @Throws(JSONException::class)
        private fun getTransactionInfo(price: String,status:String,country:String,currency:String): JSONObject {
            return JSONObject().apply {
                put("totalPrice", price)
                put("totalPriceStatus", status)
                put("countryCode", country)//Constants.COUNTRY_CODE)
                put("currencyCode",currency) //Constants.CURRENCY_CODE)
            }
        }

        fun createPaymentsClient(activity: Activity,payEnvironment:Int): PaymentsClient {
            var tempENV = Constants.PAYMENTS_ENVIRONMENT
            if (payEnvironment == 1||payEnvironment==3) {
                tempENV = payEnvironment
            }
            val walletOptions = Wallet.WalletOptions.Builder()
                .setEnvironment(tempENV)
                .build()

            return Wallet.getPaymentsClient(activity, walletOptions)
        }

        /**
         * Describe your app's support for the CARD payment method.
         *
         *
         * The provided properties are applicable to both an IsReadyToPayRequest and a
         * PaymentDataRequest.
         *
         * @return A CARD PaymentMethod object describing accepted cards.
         * @throws JSONException
         * @see [PaymentMethod](https://developers.google.com/pay/api/android/reference/object.PaymentMethod)
         */
        // Optionally, you can add billing address/phone number associated with a CARD payment method.
        private fun baseCardPaymentMethod(): JSONObject {
            return JSONObject().apply {

                val parameters = JSONObject().apply {
                    put("allowedAuthMethods", allowedCardAuthMethods)
                    put("allowedCardNetworks", allowedCardNetworks)
                    put("billingAddressRequired", true)
                    put("billingAddressParameters", JSONObject().apply {
                        put("format", "FULL")
                    })
                }

                put("type", "CARD")
                put("parameters", parameters)
            }
        }

        /**
         * An object describing accepted forms of payment by your app, used to determine a viewer's
         * readiness to pay.
         *
         * @return API version and payment methods supported by the app.
         * @see [IsReadyToPayRequest](https://developers.google.com/pay/api/android/reference/object.IsReadyToPayRequest)
         */
        fun isReadyToPayRequest(): JSONObject? {
            return try {
                val isReadyToPayRequest = JSONObject(baseRequest.toString())
                isReadyToPayRequest.put(
                    "allowedPaymentMethods", JSONArray().put(baseCardPaymentMethod()))

                isReadyToPayRequest

            } catch (e: JSONException) {
                null
            }
        }
        fun getPaymentDataRequest(configObject:GooglePayConfiguration): JSONObject? {
            val itemPriceMicros = (configObject.transactionAmount * 1000000).roundToLong()
            val price = (itemPriceMicros + configObject.shippingCost).microsToString()
            try {
                return JSONObject(baseRequest.toString()).apply {
                    val gatewayVerifone = "verifone"
                    put("allowedPaymentMethods", JSONArray().put(cardPaymentMethod(gatewayVerifone,configObject.gatewayMerchantID)))
                    put("transactionInfo", getTransactionInfo(price,configObject.priceStatus,
                        configObject.countryCode,configObject.currencyCode))
                    merchantInfo.put("merchantName", configObject.merchantName)
                    merchantInfo.put("merchantId",configObject.merchantID)
                    put("merchantInfo", merchantInfo)
                    // An optional shipping address requirement is a top-level property of the
                    // PaymentDataRequest JSON object.
                    val shippingAddressParameters = JSONObject().apply {
                        put("phoneNumberRequired", configObject.phoneNumberRequired)
                        put("allowedCountryCodes", configObject.supportedCountries)
                    }
                    put("shippingAddressRequired", true)
                    put("shippingAddressParameters", shippingAddressParameters)
                }
            } catch (e: JSONException) {
                return null
            }

        }
    }





}

/**
 * Converts micros to a string format accepted by [GooglePayUtils.getPaymentDataRequest].
 *
 * @param micros value of the price.
 */
fun Long.microsToString() = BigDecimal(this)
    .divide(GooglePayUtils.MICROS)
    .setScale(2, RoundingMode.HALF_EVEN)
    .toString()
