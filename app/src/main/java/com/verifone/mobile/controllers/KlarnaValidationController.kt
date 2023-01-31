package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.klarna.KlarnaFinalValidationResponse
import com.verifone.mobile.dataobjects.klarna.KlarnaTokenRequestObject
import com.verifone.mobile.dataobjects.klarna.KlarnaValidationRequestObject
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class KlarnaValidationController(ctx: Context, tokenParam:String, customerParam:String
                                 , transactionID:String, onAuthorizationSuccess:(response: KlarnaFinalValidationResponse) -> Unit
                                 , onAuthorizationFailed:(e: Throwable) -> Unit) {

    private val authInterceptor = BaseAuthInterceptor(
        CustomizationSettings.getPaymentsApiUserID(ctx),
        CustomizationSettings.getPaymentsApiKey(ctx))

    private var idempotencyKey = "aa2f3e42-4422-41be-bea3-5cc124a74d3e"
    private val customerID = customerParam
    private val klarnaTransactionID = transactionID
    val authToken = tokenParam
    private var resultHandler: Observer<KlarnaFinalValidationResponse> = object:
        Observer<KlarnaFinalValidationResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: KlarnaFinalValidationResponse) {
            onAuthorizationSuccess(response)
        }

        override fun onError(e: Throwable) {
           onAuthorizationFailed(e)
        }

        override fun onComplete() {
        }
    }

    fun startKlarnaTokenRequest() {
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS2,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = KlarnaValidationRequestObject(authToken)
        val uuid = UUID.randomUUID()
        idempotencyKey = uuid.toString()
        val requestObs = inputApi.startKlarnaValidationRequest(klarnaTransactionID,idempotencyKey,requestObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }
}