package br.com.pettopia.model

import com.google.gson.annotations.SerializedName

data class AnimalModel(
    @SerializedName("idAnimal") val idAnimal: Long? = null,
    @SerializedName("nome") val nome: String,
    @SerializedName("idade") val idade: String,
    @SerializedName("raca") val raca: String,
    @SerializedName("dataNascimento") val datanascc: String? = null,
    @SerializedName("cor") val cor: String,
    @SerializedName("peso") val peso : String,
    @SerializedName("especie") val especie: String,
    @SerializedName("sexo") val sexo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("fotoAnimal") val foto: ByteArray? = null,
    @SerializedName("cliente") val cliente: ClienteModel,
    )
