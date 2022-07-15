package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.CheckoutActivity
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.connectors.threeds.dataobjects.ThreedLookupObject
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.interfaces.LookupRequestDone
import com.verifone.mobile.responses.TestLookupResponse
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class LookupSimulateController(ctx: LookupRequestDone) {
    private val authInterceptor = BaseAuthInterceptor(CustomizationSettings.getPaymentsApiUserID(ctx as Context), CustomizationSettings.getPaymentsApiKey(ctx))
    private var ownerScreen: LookupRequestDone = ctx
    private val mContext = ctx as Context
    private var orderResultHandler: Observer<TestLookupResponse> = object: Observer<TestLookupResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: TestLookupResponse) {
            response.let { ownerScreen.lookupRequestSuccess(response) }
        }

        override fun onError(e: Throwable) {
            e.let { ownerScreen.lookupRequestFailed(e) }
        }

        override fun onComplete() {
        }
    }

    private fun initLookupObject(deviceID:String, encryptedCard:String, keyAlias:String,amount:Double): ThreedLookupObject {
        val requestObject = ThreedLookupObject()
        requestObject.billing_first_name = "john"
        requestObject.billing_last_name = "smith"
        requestObject.billing_phone = "5551232134"
        requestObject.billing_address_1 = "input payer address"
        requestObject.billing_city = "Ohio"
        requestObject.billing_postal_code = "321432"
        requestObject.billing_state = "OH"
        requestObject.amount = (amount*100).toInt()
        requestObject.currency_code = CheckoutActivity.currencyTV
        requestObject.card = encryptedCard
        requestObject.publicKeyAlias = keyAlias
        requestObject.threeds_contract_id = CustomizationSettings.getPaymentsContractID(mContext)
        requestObject.merchant_reference = "test reference"
        requestObject.device_info_id = deviceID
        requestObject.email = "john@gmail.com"
        requestObject.token = "3134324"
        return requestObject
    }

    fun startSimulateRequest(deviceID:String, card:String, receivedKeyAlias:String,amount:Double) {
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)


        val lookupObject = initLookupObject(deviceID,card,receivedKeyAlias,amount)
        val requestObs = inputApi.threedsLookup("application/json",lookupObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(orderResultHandler)
    }
}