package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.responses.ECOMTransactionConfirmResponse
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class VerifyECOMTransactionController(ctx: Context, onMobilePayConfirm: (responseObject: ECOMTransactionConfirmResponse) -> Unit) {

    private val mCtx = ctx

    private val authInterceptor = BaseAuthInterceptor(
        CustomizationSettings.getPaymentsApiUserID(ctx),
        CustomizationSettings.getPaymentsApiKey(ctx))

    private var resultHandler: Observer<ECOMTransactionConfirmResponse> = object:
        Observer<ECOMTransactionConfirmResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: ECOMTransactionConfirmResponse) {
            onMobilePayConfirm(response)
        }

        override fun onError(e: Throwable) {
            onMobilePayConfirm(ECOMTransactionConfirmResponse())
        }

        override fun onComplete() {
        }
    }

    fun checkTransactionStatus(transactionID:String) {

        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObs = inputApi.confirmMobilePayRequest(transactionID)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }
}