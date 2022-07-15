package com.verifone.mobile.interfaces

import com.verifone.mobile.responses.JWTResponse

interface GetJWTRequestDone {
    fun getJWTSuccess(response: JWTResponse)
    fun getJWTFailed(t:Throwable)
}