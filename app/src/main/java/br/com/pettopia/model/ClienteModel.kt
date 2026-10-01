package br.com.pettopia.model

import br.com.pettopia.Enum.generoCliente
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.LocalDateTime

data class ClienteModel(
    //@SerializedName focar eles correspoder a variavel referente a variavvel do spring boot para formar o json corretamente
    @SerializedName("idCliente") val idCliente: Long? = null,
    @SerializedName("cpf") val cpf: String? = null,
    @SerializedName("nome") val nome: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("password_Cliente") val passwordCliente: String? = null,
    @SerializedName("telefone") val telefone: String? = null,
    @SerializedName("generoCliente") val generoCliente: generoCliente? = null,
    @SerializedName("data_nascimento") val dataNascimento: String? = null,
    @SerializedName("data_Cadastro") val dataCadastro: String? = null,
    @SerializedName("resetToken") val resetToken: String? = null,
    @SerializedName("resetTokenExpiration") val resetTokenExpiration: LocalDateTime? = null
)