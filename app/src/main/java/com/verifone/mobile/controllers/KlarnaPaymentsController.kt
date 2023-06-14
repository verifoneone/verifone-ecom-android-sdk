package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.klarna.KlarnaTokenRequestObject
import com.verifone.mobile.dataobjects.klarna.KlarnaTokenResponse
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class KlarnaPaymentsController(ctx: Context, randomKeyParam:String, onTokenReceived:(clientToken:String, clientID:String, transactionID:String) -> Unit) {
    private val mCtx = ctx
    private val authInterceptor = BaseAuthInterceptor(
        CustomizationSettings.getPaymentsApiUserID(ctx),
        CustomizationSettings.getPaymentsApiKey(ctx))

    private val idempotencyKey = randomKeyParam
    val receivedToken = onTokenReceived
    private var tokenResultHandler: Observer<KlarnaTokenResponse> = object: Observer<KlarnaTokenResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: KlarnaTokenResponse) {
            val temp = response.clientToken.toString()
            receivedToken(temp,response.customer,response.id)
        }

        override fun onError(e: Throwable) {
            receivedToken("","","")
        }

        override fun onComplete() {
        }
    }

    fun startKlarnaTokenRequest(currencyParam:String) {
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS2,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = KlarnaTokenRequestObject()

        requestObject.entityID = CustomizationSettings.getPaymentOrgID(mCtx)
        requestObject.customer =CustomizationSettings.getPaymentCustomerID(mCtx)
        requestObject.currencyCode = currencyParam
        val requestObs = inputApi.startKlarnaTokenRequest(idempotencyKey,requestObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(tokenResultHandler)
    }
}