package com.verifone.mobile.controllers
import android.content.Context
import com.verifone.mobile.TestRetrofitClientInstance
import com.verifone.mobile.dataobjects.RequestJwtObject
import com.verifone.mobile.interfaces.MainAppRestApi
import com.verifone.mobile.responses.JWTResponse
import com.verifone.mobile.screens.CustomizationSettings
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class JwtCreateController(ctx: Context, onGetJWTReturned: (response: JWTResponse) -> Unit) {

    private val authInterceptor = BaseAuthInterceptor(CustomizationSettings.getPaymentsApiUserID(ctx),CustomizationSettings.getPaymentsApiKey(ctx))
    private var resultHandler: Observer<JWTResponse> = object: Observer<JWTResponse> {
        override fun onSubscribe(d: Disposable?) {
        }

        override fun onNext(response: JWTResponse) {
            onGetJWTReturned(response)
        }

        override fun onError(e: Throwable) {
            val errorResponse = JWTResponse()

            errorResponse.api_fail_message = "unknown error"
            val temp = e.localizedMessage
            if (!temp.isNullOrEmpty()&&temp.isNotEmpty()) errorResponse.api_fail_message = temp
            errorResponse.api_fail_cause = e.cause.toString()
            onGetJWTReturned(errorResponse)
        }

        override fun onComplete() {
        }
    }

    fun startCreateJWTRequest(threeds_contract_id:String) {
        val mRetrofitClient =
            TestRetrofitClientInstance.getClientInstanceAuth(TestRetrofitClientInstance.BASE_URL_CONNECTORS,authInterceptor)
        val inputApi = mRetrofitClient.create(MainAppRestApi::class.java)

        val requestObject = RequestJwtObject(threeds_contract_id)

        val requestObs = inputApi.createJWT(requestObject)

        requestObs.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .map { result->result }
            .subscribe(resultHandler)
    }

}