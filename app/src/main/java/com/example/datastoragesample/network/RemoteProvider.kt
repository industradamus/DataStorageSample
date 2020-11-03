package com.example.datastoragesample.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RemoteProvider {

    private val retrofit: Retrofit = buildRetrofit()

    fun getApi(): PixelsApi = retrofit.create(PixelsApi::class.java)

    private fun buildRetrofit(): Retrofit {
        val converterFactory: GsonConverterFactory = GsonConverterFactory.create(
            GsonBuilder()
                .setLenient()
                .create()
        )

        val callAdapterFactory: RxJava2CallAdapterFactory = RxJava2CallAdapterFactory.create()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(buildOkHttpClient())
            .addConverterFactory(converterFactory)
            .addCallAdapterFactory(callAdapterFactory)
            .build()
    }

    private fun buildOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(StethoInterceptor())
            .addInterceptor(getApiKeyInterceptor())
            .addInterceptor(HttpLoggingInterceptor())
            .readTimeout(TIMEOUT_IN_SECOND.toLong(), TimeUnit.SECONDS)
            .connectTimeout(TIMEOUT_IN_SECOND.toLong(), TimeUnit.SECONDS)
            .build()
    }

    private fun getApiKeyInterceptor() = Interceptor { chain ->
        val url = chain.request()
            .url
            .newBuilder()
            .build()

        val newRequest = chain.request()
            .newBuilder()
            .addHeader(HEADER_AUTHORIZATION, API_KEY)
            .url(url)
            .build()

        chain.proceed(newRequest)
    }

    private companion object {

        const val BASE_URL = "https://api.pexels.com/v1/"
        const val API_KEY = "563492ad6f9170000100000110380711935f47d1a68bc523780310e1"

        const val TIMEOUT_IN_SECOND = 30
        const val HEADER_AUTHORIZATION = "Authorization"
    }
}