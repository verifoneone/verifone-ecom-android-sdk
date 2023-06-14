package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.responses.vipps.VippsInitObject
import com.verifone.mobile.responses.vipps.VippsInitResponse
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class VippsInitController(ctx: Context,onVippsInitCallback: (responseObject: VippsInitResponse) -> Unit) {
    private val mCtx = ctx
    private val authInterceptor = BaseAuthInterceptor(
        CustomizationSettings.getPaymentsApiUserID(ctx),
        CustomizationSettings.getPaymentsApiKey(ctx))

    private var resultHandler: Observer<VippsInitResponse> = object:
        Observer<VippsInitResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: VippsInitResponse) {
            onVippsInitCallback(response)
        }

        override fun onError(e: Throwable) {
            onVippsInitCallback(VippsInitResponse())
        }

        override fun onComplete() {
        }
    }

    fun startVippsInitRequest(currencyParam:String) {
        val uuid = UUID.randomUUID()
        val idempotencyKey = uuid.toString()
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = VippsInitObject()
        requestObject.paymentProviderContract = CustomizationSettings.getPaymentsProviderContract(mCtx)
        requestObject.customer = CustomizationSettings.getPaymentCustomerID(mCtx)

        requestObject.currencyCode = currencyParam
        val requestObs = inputApi.initMobileVippsPay(idempotencyKey,requestObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }
}