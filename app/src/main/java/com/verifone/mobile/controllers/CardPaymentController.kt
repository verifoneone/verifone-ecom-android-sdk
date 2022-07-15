package com.verifone.mobile.controllers


import android.content.Context
import com.verifone.connectors.threeds.dataobjects.ThreedsValidationData

import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.cardpayment.CardPaymentObject
import com.verifone.mobile.dataobjects.cardpayment.TokenPaymentObject
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.responses.cardpayments.CardPaymentResponse
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CardPaymentController(ctx: Context, onCardPaymentFlow: (response: CardPaymentResponse) -> Unit) {

    private var onCardPaymentFlowComplete:(response: CardPaymentResponse) -> Unit = onCardPaymentFlow
    private val authInterceptor = BaseAuthInterceptor(CustomizationSettings.getPaymentsApiUserID(ctx), CustomizationSettings.getPaymentsApiKey(ctx))
    private val authInterceptorTokenPayments = BaseAuthInterceptor(CustomizationSettings.getPaymentsApiUserID(ctx), CustomizationSettings.getPaymentsApiKey(ctx))
    private var resultHandler: Observer<CardPaymentResponse> = object: Observer<CardPaymentResponse> {
       override fun onSubscribe(d: Disposable?) {
       }

       override fun onNext(response: CardPaymentResponse) {
           onCardPaymentFlowComplete(response)
       }

       override fun onError(e: Throwable?) {
           val errorResponse = CardPaymentResponse()
           errorResponse.api_fail_message = "unknown error"
           val temp = e?.localizedMessage
           if (!temp.isNullOrEmpty()&&temp.isNotEmpty()) errorResponse.api_fail_message = temp
           errorResponse.api_fail_cause = e?.cause.toString()
           onCardPaymentFlow(errorResponse)
       }

       override fun onComplete() {
       }
    }

    fun startTokenPaymentDataRequest(reuseToken:String,payment_provider_contract:String, amount:Double,auth_type:String,capture_now:Boolean ,merchant_reference:String, card_brand:String, currency_code:String, dynamic_descriptor:String,keyAlias:String,threedValidation: ThreedsValidationData? = null) {
        val contentType = "application/json"
        val mRetrofitClient = TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptorTokenPayments)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)


        val requestObject = TokenPaymentObject(reuseToken,payment_provider_contract,amount,auth_type,capture_now,
            merchant_reference,
            card_brand,
            currency_code,
            dynamic_descriptor,
            threedValidation,
            keyAlias
        )

        val requestObs: Observable<CardPaymentResponse> = inputApi.sendTokenPaymentData(contentType,requestObject)
        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }


    fun startCardPaymentDataRequest(encryptedCard:String,payment_provider_contract:String, amount:Double,auth_type:String,capture_now:Boolean ,merchant_reference:String, card_brand:String, currency_code:String, dynamic_descriptor:String,keyAlias:String,threedValidation: ThreedsValidationData? = null) {
        val contentType = "application/json"
        val mRetrofitClient = TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = CardPaymentObject(encryptedCard,payment_provider_contract,amount,auth_type,capture_now,
            merchant_reference,
            card_brand,
            currency_code,
            dynamic_descriptor,
            threedValidation,
            keyAlias
        )

        val requestObs: Observable<CardPaymentResponse> = inputApi.sendCardPaymentData(contentType,requestObject)
        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }

}