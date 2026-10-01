package br.com.pettopia.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.pettopia.adapter.AnimalAdapter
import br.com.pettopia.adapter.EnviarComprovanteDialogFragment
import br.com.pettopia.databinding.ActivityAdotarAnimaisBinding
import br.com.pettopia.viewModel.AnimalViewModel

class AdotarAnimaisActivity : AppCompatActivity() {

    private lateinit var viewModel: AnimalViewModel
    private lateinit var adapter: AnimalAdapter
    private val binding by lazy {
        ActivityAdotarAnimaisBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Configuração do RecyclerView
        adapter = AnimalAdapter(
            onAnimalClick = { animalId ->
                // Aqui você pode implementar o comportamento quando clicar no card do animal
                onAnimalClick(animalId)
            },
            onEnviarComprovanteClick = { animalId ->
                // Aqui você chama o DialogFragment para enviar o comprovante
                val dialog = EnviarComprovanteDialogFragment(animalId)
                dialog.show(supportFragmentManager, "EnviarComprovante")
            }
        )

        binding.recyclerViewAnimais.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewAnimais.adapter = adapter

        // Configurando ViewModel
        viewModel = ViewModelProvider(this)[AnimalViewModel::class.java]

        // Observa os dados do ViewModel
        viewModel.animaisParaAdocao.observe(this) { animais ->
            if (animais != null) {
                adapter.submitList(animais)
            } else {
                Toast.makeText(this, "Erro ao carregar animais.", Toast.LENGTH_SHORT).show()
            }
        }

        // Busca os animais
        viewModel.fetchAnimaisParaAdocao()
    }

    private fun onAnimalClick(animalId: Long) {
        // Ação a ser tomada quando um animal for clicado
        Toast.makeText(this, "Animal com ID $animalId clicado", Toast.LENGTH_SHORT).show()
        // ABRIR DETALHES
    }
}