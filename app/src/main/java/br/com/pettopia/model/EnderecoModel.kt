package br.com.pettopia.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EnderecoModel(
    @SerializedName("idEndereco")val idEndereco: Long? = null,
    @SerializedName("logradouro")val logradouro: String?,
    @SerializedName("complemento")val complemento: String? = null,
    @SerializedName("uf")val uf: String?,
    @SerializedName("localidade") val localidade: String?,
    @SerializedName("bairro")val bairro: String?,
    @SerializedName("numero")val numero: Long?,
    @SerializedName("cep")val cep: String?,
)
