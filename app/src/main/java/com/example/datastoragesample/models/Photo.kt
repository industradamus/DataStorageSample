package com.example.datastoragesample.models

import com.google.gson.annotations.SerializedName

data class Photo(

    @SerializedName("src")
    val src: Src,

    @SerializedName("width")
    val width: Int,

    @SerializedName("photographer")
    val photographer: String,

    @SerializedName("photographer_url")
    val photographerUrl: String,

    @SerializedName("id")
    val id: Int,

    @SerializedName("url")
    val url: String,

    @SerializedName("photographer_id")
    val photographerId: Int,

    @SerializedName("liked")
    val liked: Boolean,

    @SerializedName("height")
    val height: Int
)
