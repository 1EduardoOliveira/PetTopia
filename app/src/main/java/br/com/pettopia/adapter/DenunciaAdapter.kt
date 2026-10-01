package br.com.pettopia.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.pettopia.Enum.TipoDenuncia
import br.com.pettopia.databinding.ItemDenunciaBinding
import br.com.pettopia.model.DenunciaModel


class DenunciaAdapter(
    private val onExcluirClick: (Long) -> Unit,
    private val onAtualizarClick: (Long, String, String) -> Unit,
) : ListAdapter<DenunciaModel, DenunciaAdapter.DenunciaViewHolder>(DenunciaDiffCallback()) {



    inner class DenunciaViewHolder(private val binding: ItemDenunciaBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(denuncia: DenunciaModel) {


            // Exibe o tipo de denúncia corretamente
            binding.textViewTipoDenuncia.text = TipoDenuncia.fromString(String()).toString() // Exibe o tipo de denúncia no TextView

            // Exibe os outros dados da denúncia
            binding.textViewDescricao.text = denuncia.descricao
            binding.textViewStatusDenuncia.text = denuncia.statusGeral
            binding.textViewId.text = denuncia.idDenuncia.toString()
            binding.textViewData.text = denuncia.dataDenuncia

            // Botão de excluir
            binding.buttonExcluir.setOnClickListener {
                denuncia.idDenuncia?.let { it1 -> onExcluirClick(it1) }
            }

            // Botão de editar
            binding.buttonEditar.setOnClickListener {
                // Exibe o card de edição e preenche os campos com os dados da denúncia
                binding.cardEditar.visibility = View.VISIBLE
                binding.editTextTipoDenuncia.setText(TipoDenuncia.fromString(String()).getStatusDisplayName())
                binding.editTextDescricao.setText(denuncia.descricao)

                // Define o que acontece ao clicar no botão "Atualizar"
                binding.buttonAtualizar.setOnClickListener {
                    // Obtém o tipo de denúncia e a nova descrição para a atualização
                    val tipoDenunciaAtualizado = binding.editTextTipoDenuncia.text
                    val novaDescricao = binding.editTextDescricao.text.toString()

                    // Chama a função para atualizar a denúncia
                    denuncia.idDenuncia?.let { id ->
                        onAtualizarClick(
                            id,
                            tipoDenunciaAtualizado.toString(), // Passa o tipo atualizado
                            novaDescricao // Passa a nova descrição
                        )
                    }

                    // Esconde o card de edição
                    binding.cardEditar.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DenunciaViewHolder {
        val binding =
            ItemDenunciaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DenunciaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DenunciaViewHolder, position: Int) {
        val denuncia = getItem(position)
        holder.bind(denuncia)
    }

    class DenunciaDiffCallback : DiffUtil.ItemCallback<DenunciaModel>() {
        override fun areItemsTheSame(oldItem: DenunciaModel, newItem: DenunciaModel): Boolean {
            // Verifica se os IDs das denúncias são os mesmos (são identificadores únicos)
            return oldItem.idDenuncia == newItem.idDenuncia
        }

        override fun areContentsTheSame(oldItem: DenunciaModel, newItem: DenunciaModel): Boolean {
            // Compara os conteúdos dos dois objetos DenunciaModel para verificar se são iguais
            return oldItem == newItem
        }
    }
}
