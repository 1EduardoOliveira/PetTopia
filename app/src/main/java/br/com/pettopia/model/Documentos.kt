package br.com.pettopia.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime

data class Documentos(
    @SerializedName("idDocumento")val idDocumento: Long? = null,

    @SerializedName("arquivo")val arquivo: ByteArray?,

    @SerializedName("tempoExclusao")val tempoExclusao: LocalDateTime? = null,

    @SerializedName("status")val status: String?,

    @SerializedName("cliente")val cliente: ClienteModel?,

    @SerializedName("animal")val animal: AnimalModel?

)
