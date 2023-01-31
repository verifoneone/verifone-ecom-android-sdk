package com.verifone.mobile.interfaces


import com.verifone.mobile.dataobjects.RequestJwtObject
import com.verifone.mobile.dataobjects.ReuseTokenRequest
import com.verifone.connectors.threeds.dataobjects.ThreedLookupObject
import com.verifone.mobile.dataobjects.cardpayment.CardPaymentObject
import com.verifone.mobile.dataobjects.cardpayment.TokenPaymentObject
import com.verifone.mobile.dataobjects.googlepayments.GooglePayObject
import com.verifone.mobile.dataobjects.googlepayments.GooglePayResponse
import com.verifone.mobile.dataobjects.klarna.KlarnaFinalValidationResponse
import com.verifone.mobile.dataobjects.klarna.KlarnaTokenRequestObject
import com.verifone.mobile.dataobjects.klarna.KlarnaTokenResponse
import com.verifone.mobile.dataobjects.klarna.KlarnaValidationRequestObject
import com.verifone.mobile.dataobjects.mobilepay.InitMobilePayResponse
import com.verifone.mobile.responses.ECOMTransactionConfirmResponse
import com.verifone.mobile.dataobjects.mobilepay.MobilePayInitObject
import com.verifone.mobile.dataobjects.paypal.PayPalConfirmationObject
import com.verifone.mobile.dataobjects.paypal.PayPalRequestObject
import com.verifone.mobile.dataobjects.swish.SwishTokenRequestObject
import com.verifone.mobile.dataobjects.swish.SwishTokenResponse
import com.verifone.mobile.dataobjects.threeds.DecodeThreedsRequest
import com.verifone.mobile.dataobjects.threeds.DecodedThreedsData
import com.verifone.mobile.responses.paypal.PayPalResponse
import com.verifone.mobile.responses.JWTResponse
import com.verifone.mobile.responses.ReuseTokenResponse
import com.verifone.mobile.responses.TestLookupResponse
import com.verifone.mobile.responses.cardpayments.CardPaymentResponse
import com.verifone.mobile.responses.paypal.PayPalConfirmationResponse
import com.verifone.mobile.responses.vipps.VippsInitObject
import com.verifone.mobile.responses.vipps.VippsInitResponse
import io.reactivex.Observable
import retrofit2.http.*

interface MainAppRestApi {
    @POST("/oidc/3ds-service/v2/jwt/create")
    fun createJWT(@Body dataParams:RequestJwtObject): Observable<JWTResponse>

    @POST("/oidc/3ds-service/v2/lookup")
    fun threedsLookup(@Header("Content-Type") contentType:String,@Body dataParams: ThreedLookupObject):Observable<TestLookupResponse>

    @POST("/oidc/3ds-service/v2/jwt/validate")
    fun threedsDecode(@Header("Content-Type") contentType:String,@Body dataParams: DecodeThreedsRequest):Observable<DecodedThreedsData>

    @POST("/oidc/api/v2/transactions/card")
    fun sendCardPaymentData(@Header("Content-Type") contentType:String,  @Body params: CardPaymentObject): Observable<CardPaymentResponse>

    @POST("/oidc/api/v2/transactions/card")
    fun sendTokenPaymentData(@Header("Content-Type") contentType:String, @Body params: TokenPaymentObject): Observable<CardPaymentResponse>

    @PUT("/oidc/api/v2/card")
    fun getReuseToken(@Header("Content-Type") contentType:String,   @Body params: ReuseTokenRequest): Observable<ReuseTokenResponse>

    @POST("/oidc/paypal-ecom/transactions")
    fun startPayPalRequest(@Body dataParams:PayPalRequestObject): Observable<PayPalResponse>

    @POST("/oidc/paypal-ecom/transactions/{id}/authorize")
    fun startPayPalConfirmation(@Path("id")token:String, @Header("x-vfi-api-idempotencyKey")idempotencyKey:String, @Body dataParams:PayPalConfirmationObject): Observable<PayPalConfirmationResponse>

    @POST("/oidc/api/v2/transactions/wallet")
    fun sendGooglePaymentData(@Header("Content-Type") contentType:String, @Body params: GooglePayObject): Observable<GooglePayResponse>

    @POST("/oidc/api/v2/transactions/klarna")
    fun startKlarnaTokenRequest(@Header("x-vfi-api-idempotencyKey")idempotencyKey:String, @Body dataParams:KlarnaTokenRequestObject): Observable<KlarnaTokenResponse>

    @POST("/oidc/api/v2/transactions/{customerID}/klarna_complete")
    fun startKlarnaValidationRequest(@Path("customerID")customer:String, @Header("x-vfi-api-idempotencyKey")idempotencyKey:String, @Body dataParams:KlarnaValidationRequestObject): Observable<KlarnaFinalValidationResponse>

    @POST("/oidc/api/v2/transactions/swish")
    fun startSwishTokenRequest(@Header("x-vfi-api-idempotencyKey")idempotencyKey:String, @Body dataParams:SwishTokenRequestObject): Observable<SwishTokenResponse>

    @POST("/oidc/api/v2/transactions/mobilepay")
    fun initMobilePayRequest(@Header("x-vfi-api-idempotencyKey")idempotencyKey:String, @Body dataParams:MobilePayInitObject): Observable<InitMobilePayResponse>

    @GET("/oidc/api/v2/transaction/{id}")
    fun confirmMobilePayRequest(@Path("id")transactionId:String): Observable<ECOMTransactionConfirmResponse>

    @POST("/oidc/api/v2/transactions/vipps")
    fun initMobileVippsPay(@Header("x-vfi-api-idempotencyKey")idempotencyKey:String, @Body dataParams:VippsInitObject): Observable<VippsInitResponse>
}