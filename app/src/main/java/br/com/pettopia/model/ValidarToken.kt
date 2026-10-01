package br.com.pettopia.model

import com.google.gson.annotations.SerializedName

data class ValidarToken(
    @SerializedName("token") val token: String? = null,
    @SerializedName("email") val email: String? = null
)
