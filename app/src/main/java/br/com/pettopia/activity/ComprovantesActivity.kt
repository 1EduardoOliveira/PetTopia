package br.com.pettopia.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager

import br.com.pettopia.R
import br.com.pettopia.adapter.Comprovantes
import br.com.pettopia.databinding.ActivityComprovantesBinding
import br.com.pettopia.model.ServicosModel
import br.com.pettopia.viewModel.ClienteViewModel

class ComprovantesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityComprovantesBinding
    private lateinit var comprovanteAdapter: Comprovantes
    private lateinit var viewModel: ClienteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityComprovantesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa o ViewModel
        viewModel = ViewModelProvider(this).get(ClienteViewModel::class.java)

        // Configura o RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        comprovanteAdapter = Comprovantes(emptyList(), viewModel)
        binding.recyclerView.adapter = comprovanteAdapter

        // Recupera o ID do usuário do SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val idCliente = sharedPreferences.getLong("idUsuario", 0L)

        if (idCliente != 0L) {
            // Busca os comprovantes do servidor via ViewModel
            viewModel.getComprovantes(idCliente)
        } else {
            Toast.makeText(this, "ID do usuário não encontrado.", Toast.LENGTH_SHORT).show()
        }

        // Observa as mudanças nos dados
        observeViewModel()
    }


    private fun observeViewModel() {
        viewModel.comprovantes.observe(this) { comprovantes ->
            if (comprovantes != null && comprovantes.isNotEmpty()) {
                Log.d("ComprovantesActivity", "Comprovantes recebidos: ${comprovantes.size}")
                comprovanteAdapter.submitList(comprovantes)
            } else {
                Log.d("ComprovantesActivity", "Nenhum comprovante encontrado.")
                Toast.makeText(this, "Nenhum comprovante encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                Log.e("ComprovantesActivity", "Erro: $error")
                Toast.makeText(this, "Erro: $error", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
