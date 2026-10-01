package br.com.pettopia.adapter

import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.pettopia.databinding.ItemAnimalBinding
import br.com.pettopia.model.AnimalModel

class AnimalAdapter(
    private val onAnimalClick: (Long) -> Unit,
    private val onEnviarComprovanteClick: (Long) -> Unit
) : ListAdapter<AnimalModel, AnimalAdapter.AnimalViewHolder>(AnimalDiffCallback()) {

    inner class AnimalViewHolder(private val binding: ItemAnimalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(animal: AnimalModel) {
            Log.d("AnimalAdapter", "Animal: $animal")

            // Verifica se o animal tem uma foto (se nÃ£o for null)
            animal.foto?.let { fotoAnimalByteArray ->
                Log.d("AnimalAdapter", "Foto: $fotoAnimalByteArray")

                // Decodifica o ByteArray para Bitmap
                val bitmap = BitmapFactory.decodeByteArray(fotoAnimalByteArray, 0, fotoAnimalByteArray.size)
                binding.imageViewFoto.setImageBitmap(bitmap) // Exibe a imagem no ImageView
                Log.d("AnimalAdapter", "Foto exibida")
            }

            // Preenche os dados do animal
            binding.textViewNome.text = animal.nome
            binding.textViewDescricao.text = animal.descricao
            binding.textViewIdade.text = "${animal.idade} anos"

            Log.d("AnimalAdapter", "Dados do animal preenchidos")
            Log.d("AnimalAdapter", "Nome: ${animal}")
            Log.d("AnimalAdapter", "Foto: ${animal.foto}")


            binding.btnEnviarComprovante.setOnClickListener {
                animal.idAnimal?.let { it1 -> onEnviarComprovanteClick(it1) }
                Log.d("AnimalAdapter", "Botão de enviar comprovante clicado")
                Log.d("AnimalAdapter", "ID do animal: ${animal.idAnimal}")
            }
            // Configura o clique no item
            binding.root.setOnClickListener {
                animal.idAnimal?.let { it1 -> onAnimalClick(it1) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimalViewHolder {
        val binding = ItemAnimalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AnimalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimalViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AnimalDiffCallback : DiffUtil.ItemCallback<AnimalModel>() {
        override fun areItemsTheSame(oldItem: AnimalModel, newItem: AnimalModel): Boolean {
            return oldItem.idAnimal == newItem.idAnimal
        }

        override fun areContentsTheSame(oldItem: AnimalModel, newItem: AnimalModel): Boolean {
            return oldItem == newItem
        }
    }
}