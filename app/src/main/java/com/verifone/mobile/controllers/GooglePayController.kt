package com.verifone.mobile.controllers


import android.content.Context
import com.verifone.connectors.googlepay.WalletPayloadObject
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.googlepayments.GooglePayObject
import com.verifone.mobile.dataobjects.googlepayments.GooglePayResponse
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class GooglePayController(ctx: Context,
                          onGooglePaySuccess: (GooglePayResponse) -> Unit,
                          onGooglePayFail: (errorMessage: String) -> Unit
) {
    private val authInterceptor = BaseAuthInterceptor(CustomizationSettings.getPaymentsApiUserID(ctx), CustomizationSettings.getPaymentsApiKey(ctx))
    private var resultHandler: Observer<GooglePayResponse> = object: Observer<GooglePayResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(value: GooglePayResponse) {
            onGooglePaySuccess(value)
        }

        override fun onError(e: Throwable?) {
            onGooglePayFail(e?.localizedMessage.toString())
        }

        override fun onComplete() {
        }
    }

    fun startPaymentDataRequest(payment_provider_contract:String, amount:Int,auth_type:String,capture_now:Boolean , customer_ip:String,merchant_reference:String, card_brand:String, shopper_interaction:String, currency_code:String, dynamic_descriptor:String,wallet_type:String,walletPayload: WalletPayloadObject) {
        val contentType = "application/json"

        val mRetrofitClient = TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS2,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)
        //val mAuth3D = ThreedAuthentication()

        val requestObject = GooglePayObject(payment_provider_contract,amount,auth_type,capture_now,customer_ip,
            merchant_reference,
            card_brand,
            shopper_interaction,
            currency_code,
            dynamic_descriptor,
            wallet_type,
            walletPayload
        )

        val requestObs: Observable<GooglePayResponse> = inputApi.sendGooglePaymentData(contentType,requestObject)
        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }



}