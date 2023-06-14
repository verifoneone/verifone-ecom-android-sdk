package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.swish.SwishTokenRequestObject
import com.verifone.mobile.dataobjects.swish.SwishTokenResponse
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class GetSwishTokenController(ctx: Context,onSwishTokenGet: (transactionID:String, token:String) -> Unit) {


    private val authInterceptor = BaseAuthInterceptor(
        CustomizationSettings.getPaymentsApiUserID(ctx),
        CustomizationSettings.getPaymentsApiKey(ctx))

    private val mCtx = ctx

    private var resultHandler: Observer<SwishTokenResponse> = object: Observer<SwishTokenResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: SwishTokenResponse) {
            onSwishTokenGet(response.id,response.paymentRequestToken)
        }

        override fun onError(e: Throwable) {
            onSwishTokenGet("","")
        }

        override fun onComplete() {
        }
    }

    fun startGetSwishTokenRequest(currencyParam:String) {
        val uuid = UUID.randomUUID()
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = SwishTokenRequestObject()
        requestObject.paymentProviderContract = CustomizationSettings.getPaymentsProviderContract(mCtx)
        requestObject.entityId = CustomizationSettings.getPaymentOrgID(mCtx)

        requestObject.currencyCode = currencyParam

        val requestObservable = inputApi.startSwishTokenRequest(uuid.toString(),requestObject)

        requestObservable.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }
}