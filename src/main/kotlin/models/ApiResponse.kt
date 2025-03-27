package com.univali.models

import com.google.gson.annotations.SerializedName

data class ApiResponse(
    var titulo: String? = null,
    var ano: Int? = null,
    var sinopse: String? = null,
    var reviews: List<String?>? = emptyList(),
)
