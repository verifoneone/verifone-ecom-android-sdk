package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.paypal.PayPalConfirmationObject
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.responses.paypal.PayPalConfirmationResponse
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class PayPalConfirmationController(ctx: Context, onPayPalConfirmation: (response: PayPalConfirmationResponse) -> Unit) {
    private var xVfiApiIdempotencyKey = ""
    private val merchantRef = "DD123-reference"
    private val authInterceptor = BaseAuthInterceptor(CustomizationSettings.getPaymentsApiUserID(ctx), CustomizationSettings.getPaymentsApiKey(ctx))
    private var payPalConfirmationResultHandler: Observer<PayPalConfirmationResponse> = object: Observer<PayPalConfirmationResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: PayPalConfirmationResponse) {
            onPayPalConfirmation(response)
        }

        override fun onError(e: Throwable) {
            val emptyResponse = PayPalConfirmationResponse()
            emptyResponse.isError = true
            emptyResponse.errorMessage = e.message.toString()
            onPayPalConfirmation(emptyResponse)
        }

        override fun onComplete() {

        }
    }

    fun startPayPalConfirmation(confirmationID:String) {
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS2,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = PayPalConfirmationObject(merchantRef)
        val uuid = UUID.randomUUID()
        xVfiApiIdempotencyKey = uuid.toString()
        val requestObs = inputApi.startPayPalConfirmation(confirmationID,xVfiApiIdempotencyKey,requestObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(payPalConfirmationResultHandler)
    }
}