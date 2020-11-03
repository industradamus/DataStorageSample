package com.example.datastoragesample.models

import com.google.gson.annotations.SerializedName

data class PixelsResponse(

    @SerializedName("next_page")
    val nextPage: String,

    @SerializedName("per_page")
    val perPage: Int,

    @SerializedName("page")
    val page: Int,

    @SerializedName("photos")
    val photos: List<Photo>,

    @SerializedName("total_results")
    val totalResults: Int
)

