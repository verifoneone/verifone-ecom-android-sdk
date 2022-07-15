package com.verifone.mobile.controllers

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class BaseAuthInterceptor(user:String, password:String):Interceptor {
    private lateinit var credentials: String
    val authUser = user
    val authPassword = password
    override fun intercept(chain: Interceptor.Chain): Response {
        credentials = Credentials.basic(authUser,authPassword)
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials).build()
        return chain.proceed(authenticatedRequest)
    }
}