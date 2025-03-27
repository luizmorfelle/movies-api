package com.univali.models.tmdb

import com.google.gson.annotations.SerializedName

data class TmdbResponseModel(
    @SerializedName("page") var page: Int? = null,
    @SerializedName("results") var results: ArrayList<TmdbResultResponseModel> = arrayListOf(),
    @SerializedName("total_pages") var totalPages: Int? = null,
    @SerializedName("total_results") var totalResults: Int? = null
)
