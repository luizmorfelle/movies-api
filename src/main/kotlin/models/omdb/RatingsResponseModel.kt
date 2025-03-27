package com.univali.models.omdb

import com.google.gson.annotations.SerializedName

data class RatingsResponseModel(
    @SerializedName("Source")
    var source: String? = null,
    @SerializedName("Value")
    var value: String? = null
)