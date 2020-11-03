package com.example.datastoragesample.network

import com.example.datastoragesample.models.PixelsResponse
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface PixelsApi {

    @GET("search/?per_page=15&query=people")
    fun getImageList(@Query("page") page: Int): Single<PixelsResponse>
}