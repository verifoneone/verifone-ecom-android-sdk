package com.verifone.mobile

import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


internal object TestRetrofitClientInstance {


    const val CST_REGION = "https://cst.test-gsc.vfims.com"
    const val CST_REGION2 = "https://cst2.test-gsc.vfims.com"

    const val US_CST_REGION = "https://uscst-gb.gsc.vficloud.net"
    const val EMEA_PROD_REGION = "https://gsc.verifone.cloud"
    const val US_PROD_REGION = "https://us.gsc.verifone.cloud"
    const val NZ_PROD_REGION = "https://nz.gsc.verifone.cloud"

    var BASE_URL_CONNECTORS = CST_REGION
    var BASE_URL_CONNECTORS2 = CST_REGION2




    private val httpClientOK: OkHttpClient
        get() {
            val logsInterceptor = HttpLoggingInterceptor()
            logsInterceptor.level = HttpLoggingInterceptor.Level.BODY
            return OkHttpClient.Builder()
                .addInterceptor(logsInterceptor)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
        }


    fun getInstance(baseUrl: String?): Retrofit {
        val retValue: Retrofit
        val logsInterceptor = HttpLoggingInterceptor()
        logsInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder()
            .addInterceptor(logsInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        retValue = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClientOK)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retValue
    }

    fun getClientInstanceAuth(baseUrl: String?,authInterceptor:Interceptor): Retrofit {
        val retValue: Retrofit
        val logsInterceptor = HttpLoggingInterceptor()
        logsInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val clientRet = OkHttpClient.Builder()
            .addInterceptor(logsInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        val gson = GsonBuilder()
            .setLenient()
            .create()
        retValue = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(clientRet)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retValue
    }
}
