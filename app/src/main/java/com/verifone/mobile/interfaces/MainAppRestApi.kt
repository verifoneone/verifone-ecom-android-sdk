package com.verifone.mobile.interfaces


import com.verifone.mobile.dataobjects.RequestJwtObject
import com.verifone.mobile.dataobjects.ReuseTokenRequest
import com.verifone.connectors.threeds.dataobjects.ThreedLookupObject
import com.verifone.mobile.dataobjects.cardpayment.CardPaymentObject
import com.verifone.mobile.dataobjects.cardpayment.TokenPaymentObject
import com.verifone.mobile.dataobjects.googlepayments.GooglePayObject
import com.verifone.mobile.dataobjects.googlepayments.GooglePayResponse
import com.verifone.mobile.dataobjects.paypal.PayPalConfirmationObject
import com.verifone.mobile.dataobjects.paypal.PayPalRequestObject
import com.verifone.mobile.responses.paypal.PayPalResponse
import com.verifone.mobile.responses.JWTResponse
import com.verifone.mobile.responses.ReuseTokenResponse
import com.verifone.mobile.responses.TestLookupResponse
import com.verifone.mobile.responses.cardpayments.CardPaymentResponse
import com.verifone.mobile.responses.paypal.PayPalConfirmationResponse
import io.reactivex.Observable
import retrofit2.http.*

interface MainAppRestApi {
    @POST("/oidc/3ds-service/v2/jwt/create")
    fun createJWT(@Body dataParams:RequestJwtObject): Observable<JWTResponse>

    @POST("/oidc/3ds-service/v2/lookup")
    fun threedsLookup(@Header("Content-Type") contentType:String,@Body dataParams: ThreedLookupObject):Observable<TestLookupResponse>

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


}