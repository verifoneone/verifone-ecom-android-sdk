package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.Keys
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.ReuseTokenRequest
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.responses.ReuseTokenResponse
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RecurrentPayController(
    ctx: Context,
    brand: String,
    reuseTokenKey: String,
    onReuseTokenCallback: (api_response: String) -> Unit
) {

    private val authInterceptor = BaseAuthInterceptor(
        CustomizationSettings.getPaymentsApiUserID(ctx),
        CustomizationSettings.getPaymentsApiKey(ctx)
    )

    private val contentType = "application/json"
    lateinit var reuseCardToken: ReuseTokenResponse
    lateinit var errorMessage: String
    private val mCtx = ctx
    val cardBrand = brand
    val reuseTokenDone = onReuseTokenCallback
    private var resultHandler: io.reactivex.Observer<ReuseTokenResponse> = object :
        io.reactivex.Observer<ReuseTokenResponse> {
        override fun onSubscribe(d: Disposable?) {

        }

        override fun onNext(valueResponse: ReuseTokenResponse) {
            reuseCardToken = valueResponse
            saveCardDetails(valueResponse.reuseToken, cardBrand, reuseTokenKey)
            reuseTokenDone(valueResponse.tokenStatus)
        }

        override fun onError(e: Throwable?) {
            errorMessage = e?.message.toString()
            reuseTokenDone(errorMessage)
        }

        override fun onComplete() {

        }
    }

    fun getReuseTokenRequest(
        encryptedCardParam: String,
        tokenScopeParam: String,
        publicKeyAliasParam: String,
        tokenTypeParam: String,
        tokenExpiry: String
    ) {
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(
                TestRetrofitClientInstance.BASE_URL_CONNECTORS,
                authInterceptor
            )
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = ReuseTokenRequest()
        requestObject.encryptedCard = encryptedCardParam
        requestObject.publicKeyAlias = publicKeyAliasParam
        //requestObject.tokenExpiryDate = tokenExpiry
        requestObject.tokenScope = tokenScopeParam
        requestObject.tokenType = tokenTypeParam

        val requestObs = inputApi.getReuseToken(contentType, requestObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result -> result }
            .subscribe(resultHandler)
    }

    private fun saveCardDetails(token: String, brand: String, reuseTokenForKey: String) {
        val sp = mCtx.getSharedPreferences("checkout_data", Context.MODE_PRIVATE)
        sp.edit()
            .putString(reuseTokenForKey, token)
            .putString(Keys.keyCardBrand, brand)
            .apply()
    }

}