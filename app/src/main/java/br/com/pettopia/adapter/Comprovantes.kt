package br.com.pettopia.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import br.com.pettopia.Enum.statusAdocao
import br.com.pettopia.R
import br.com.pettopia.model.ServicosModel
import br.com.pettopia.viewModel.ClienteViewModel

class Comprovantes(private var comprovantes: List<ServicosModel> ,private val servicosViewModel: ClienteViewModel, ) : RecyclerView.Adapter<Comprovantes.ComprovanteViewHolder>(){


    // ViewHolder: Representa os itens do RecyclerView
    class ComprovanteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCodigoComprovante: TextView = itemView.findViewById(R.id.tvCodigoComprovante)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val tvData: TextView = itemView.findViewById(R.id.tvData)
        val tvBaixar: Button = itemView.findViewById(R.id.buttonBaixar)
    }

    fun submitList(newList: List<ServicosModel>) {
        comprovantes = newList
        notifyDataSetChanged() // Notifica o RecyclerView sobre a mudança na lista
    }

    // Infla o layout do item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComprovanteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_documentos_novo, parent, false)
        return ComprovanteViewHolder(view)
    }

    // Vincula os dados do modelo ao item
    override fun onBindViewHolder(holder: ComprovanteViewHolder, position: Int) {
        val comprovante = comprovantes[position]

        // Exibe o status do pedido com a função getStatusDisplayName

        val statusString = "ANDAMENTO"  // Exemplo, você vai obter isso da resposta da API
        val status = statusAdocao.fromString(statusString)

        holder.tvStatus.text = status.getStatusDisplayName()

        // Definindo os dados do item
        holder.tvCodigoComprovante.text = comprovante.codigoComprovante ?: "Sem código"
        holder.tvData.text = comprovante.dataPedido ?: "sem data"

        holder.tvBaixar.setOnClickListener {
            // Chama a função baixarComprovante no ViewModel
            servicosViewModel.baixarComprovante(comprovante, holder.itemView.context)
        }
    }

    // Retorna o tamanho da lista
    override fun getItemCount(): Int {
        return comprovantes.size
    }
}
