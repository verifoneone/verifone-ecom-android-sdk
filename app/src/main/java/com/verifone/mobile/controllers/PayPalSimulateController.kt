package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.CheckoutActivity
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.paypal.PayPalRequestObject
import com.verifone.mobile.dataobjects.paypal.PurchasedItem
import com.verifone.mobile.responses.paypal.PayPalResponse
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.screens.CustomizationSettings

import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PayPalSimulateController(ctx:Context,onPayPalSuccess: (response: PayPalResponse) -> Unit, onPayPalApiError: (error: String) -> Unit) {
    private val mCtx = ctx
    private val authInterceptor = BaseAuthInterceptor(CustomizationSettings.getPaymentsApiUserID(ctx), CustomizationSettings.getPaymentsApiKey(ctx))
    lateinit var requestObject:PayPalRequestObject
    private var resultHandler: Observer<PayPalResponse> = object: Observer<PayPalResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: PayPalResponse) {
            onPayPalSuccess(response)
        }

        override fun onError(e: Throwable) {
            val errorResponse = e.localizedMessage
            onPayPalApiError(errorResponse)
        }

        override fun onComplete() {
        }
    }

    fun startPayPalApi(paramItems:ArrayList<PurchasedItem>) {
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS2,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)
        val ppc = CustomizationSettings.getPaymentsProviderContractPaypal(mCtx)
        val dynamicDescriptor = "PayPal order DD123"
        val merchantReference = "DD123-reference"
        requestObject = PayPalRequestObject(CheckoutActivity.currencyTV,ppc,dynamicDescriptor,merchantReference,paramItems)
        val requestObs = inputApi.startPayPalRequest(requestObject)
        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }

}