package br.com.pettopia.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("cliente") val cliente: ClienteModel
)
