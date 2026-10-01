package br.com.pettopia.model

import br.com.pettopia.Enum.statusAdocao
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class ServicosModel(
    @SerializedName("idPedido") val idPedido: Long?,
    @SerializedName("StatusPedido") val statusPedido: statusAdocao?,
    @SerializedName("comprovante") val comprovanteBase64: String?, // Usando String para Base64
    @SerializedName("codigoComprovante") val codigoComprovante: String?,
    @SerializedName("tipo") val tipo: String?,
    @SerializedName("dataPedido") val dataPedido: String?,  // Ajuste conforme o formato real da data
    @SerializedName("valido") val valido: Boolean?
) {
    fun getComprovanteAsByteArray(): ByteArray? {
        return comprovanteBase64?.let { android.util.Base64.decode(it, android.util.Base64.DEFAULT) }
    }
}

