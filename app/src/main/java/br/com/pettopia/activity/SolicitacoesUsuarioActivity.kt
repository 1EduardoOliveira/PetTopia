package br.com.pettopia.activity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.pettopia.R
import br.com.pettopia.adapter.DocumentosAdapter
import br.com.pettopia.databinding.ActivitySolicitacoesUsuarioBinding
import br.com.pettopia.model.Documentos
import br.com.pettopia.viewModel.ClienteViewModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SolicitacoesUsuarioActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySolicitacoesUsuarioBinding.inflate(layoutInflater)
    }

    private lateinit var documentosAdapter: DocumentosAdapter
    private lateinit var documentosViewModel: ClienteViewModel
    private var documentosList: List<Documentos> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura a RecyclerView no ViewBinding
        binding.recyclerViewDocumentos.layoutManager = LinearLayoutManager(this)

        // Inicializa o ViewModel
        documentosViewModel = ViewModelProvider(this).get(ClienteViewModel::class.java)

        // Observa a lista de documentos para atualizar a RecyclerView
        documentosViewModel.documentosList.observe(this, Observer { documentos ->
            documentosList = documentos
            documentosAdapter = DocumentosAdapter(documentosList)
            binding.recyclerViewDocumentos.adapter = documentosAdapter
        })

        // ID do cliente (por exemplo)
        val sharedPreferences = applicationContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val idCliente = sharedPreferences.getLong("idUsuario", 0L)
        Log.d("SolicitacoesUsuarioActivity", "ID do Cliente: $idCliente")

        // Chama o método do ViewModel para carregar os documentos
        documentosViewModel.listarDocumentosCliente(idCliente)
    }
}