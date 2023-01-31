package com.verifone.mobile.controllers

import android.content.Context
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.threeds.DecodeThreedsRequest
import com.verifone.mobile.dataobjects.threeds.DecodedThreedsData
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class ThreedsDecodeController(
    ctx: Context,
    onDecodedThreedsReturned: (threedsData: DecodedThreedsData) -> Unit
) {
    var mCtx:Context = ctx
    var onReturnResult = onDecodedThreedsReturned

    private val authInterceptor = BaseAuthInterceptor(
        CustomizationSettings.getPaymentsApiUserID(ctx),
        CustomizationSettings.getPaymentsApiKey(ctx))
    private var resultHandler: Observer<DecodedThreedsData> = object: Observer<DecodedThreedsData> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: DecodedThreedsData) {
            onReturnResult(response)
        }

        override fun onError(e: Throwable) {
            val temp = e.localizedMessage
            var apiFailMessage = ""
            if (!temp.isNullOrEmpty()&&temp.isNotEmpty()) apiFailMessage = temp
            val apiFailCause = e.cause.toString()
            val emptyResult = DecodedThreedsData()
            emptyResult.errorNO = apiFailMessage
            emptyResult.errorDesc = apiFailCause
            onReturnResult(emptyResult)

        }

        override fun onComplete() {
        }
    }

    fun startDecodeThreedsRequest(threeds_contract_id:String,jwtParam:String,authIDParam:String) {
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = DecodeThreedsRequest()
        requestObject.jwt = jwtParam
        requestObject.authID = authIDParam
        requestObject.threedsContractID = threeds_contract_id
        val contentType = "application/json"
        val requestObs = inputApi.threedsDecode(contentType,requestObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }
}