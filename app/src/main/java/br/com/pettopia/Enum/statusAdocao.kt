package br.com.pettopia.Enum

enum class statusAdocao {
    ANDAMENTO,
    CONCLUIDO,
    PENDENTE,
    CANCELADO;

    fun getStatusDisplayName(): String {
        return when (this) {
            ANDAMENTO -> "Em andamento"
            CONCLUIDO -> "Concluído"
            PENDENTE -> "Pendente"
            CANCELADO -> "Cancelado"
        }
    }

    // Função de fallback para status nulo
    companion object {
        fun fromString(status: String?): statusAdocao {
            return when (status) {
                "ANDAMENTO" -> ANDAMENTO
                "CONCLUIDO" -> CONCLUIDO
                "PENDENTE" -> PENDENTE
                "CANCELADO" -> CANCELADO
                else -> ANDAMENTO
            }
        }
    }

}


