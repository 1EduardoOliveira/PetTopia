package br.com.pettopia.Enum

import br.com.pettopia.Enum.statusAdocao.ANDAMENTO
import br.com.pettopia.Enum.statusAdocao.CANCELADO
import br.com.pettopia.Enum.statusAdocao.CONCLUIDO
import br.com.pettopia.Enum.statusAdocao.PENDENTE

enum class TipoDenuncia() {
    VIOLENCIA,
    PERDIDO,
    ABANDONADO;

    fun getStatusDisplayName(): String {
        return when (this) {
            VIOLENCIA -> "VIOLENCIA"
            PERDIDO -> "PERDIDO"
            ABANDONADO -> "ABANDONADO"
        }
    }

    companion object {
        // Função para converter uma string para o Enum correspondente
        fun fromString(tipoDenuncia: String?): TipoDenuncia {
            return when (tipoDenuncia) {
                "VIOLENCIA" -> VIOLENCIA
                "PERDIDO" -> PERDIDO
                "ABANDONADO" -> ABANDONADO
                else -> PERDIDO  // Valor padrão
            }
        }
    }
}
