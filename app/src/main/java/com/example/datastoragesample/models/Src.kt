package com.example.datastoragesample.models

import com.google.gson.annotations.SerializedName

data class Src(

    @SerializedName("small")
    val small: String,

    @SerializedName("original")
    val original: String,

    @SerializedName("large")
    val large: String,

    @SerializedName("tiny")
    val tiny: String,

    @SerializedName("medium")
    val medium: String,

    @SerializedName("large2x")
    val large2x: String,

    @SerializedName("portrait")
    val portrait: String,

    @SerializedName("landscape")
    val landscape: String
)
