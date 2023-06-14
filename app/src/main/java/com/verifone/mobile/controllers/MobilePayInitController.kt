package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.mobilepay.InitMobilePayResponse
import com.verifone.mobile.dataobjects.mobilepay.MobilePayInitObject
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class MobilePayInitController(ctx: Context,onMobilePayInit: (responseObject:InitMobilePayResponse) -> Unit) {
    private val mCtx = ctx

    private val authInterceptor = BaseAuthInterceptor(
        CustomizationSettings.getPaymentsApiUserID(ctx),
        CustomizationSettings.getPaymentsApiKey(ctx))

    private var resultHandler: Observer<InitMobilePayResponse> = object: Observer<InitMobilePayResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: InitMobilePayResponse) {
            onMobilePayInit(response)
        }

        override fun onError(e: Throwable) {
            onMobilePayInit(InitMobilePayResponse())
        }

        override fun onComplete() {
        }
    }

    fun startMobilePayRequest(currencyParam:String) {
        val uuid = UUID.randomUUID()
        val idempotencyKey = uuid.toString()
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = MobilePayInitObject()
        requestObject.paymentProviderContract = CustomizationSettings.getPaymentsProviderContract(mCtx)
        requestObject.customer = CustomizationSettings.getPaymentCustomerID(mCtx)
        requestObject.currencyCode = currencyParam

        val requestObs = inputApi.initMobilePayRequest(idempotencyKey,requestObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }
}