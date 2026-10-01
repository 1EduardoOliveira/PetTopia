package br.com.pettopia.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password_Cliente") val passwordCliente: String,
)
