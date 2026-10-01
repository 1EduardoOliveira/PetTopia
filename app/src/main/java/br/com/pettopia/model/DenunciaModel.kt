package br.com.pettopia.model

import br.com.pettopia.Enum.TipoDenuncia
import com.google.gson.annotations.SerializedName

data class DenunciaModel(
    @SerializedName("idDenuncia")val idDenuncia: Long? = null,

    @SerializedName("tipoDenucias") val tipoDenucias: String,

    @SerializedName("descricao")val descricao: String?,

    @SerializedName("dataDenuncia")val dataDenuncia: String? = null,

    @SerializedName("statusGeral")val statusGeral: String? = null,

    @SerializedName("cliente")val cliente: ClienteModel? = null,

    @SerializedName("endereco")val endereco: EnderecoModel? = null
)
