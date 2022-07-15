package com.verifone.mobile.interfaces

import com.verifone.mobile.responses.cardpayments.CardPaymentResponse

interface CardPayInterface {
    fun onCardPaySuccess(response: CardPaymentResponse)
    fun onCardPayFailed(error:Throwable?)
}