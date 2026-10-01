package br.com.pettopia.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.pettopia.R
import br.com.pettopia.adapter.DenunciaAdapter
import br.com.pettopia.viewModel.ClienteViewModel


class VisualizarDenunciaActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recyclerView: RecyclerView
    private lateinit var denunciaAdapter: DenunciaAdapter
    private lateinit var viewModel: ClienteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizar_denuncia)


        // Inicialização do ViewModel
        viewModel = ViewModelProvider(this).get(ClienteViewModel::class.java)

        // Configuração do RecyclerView
        recyclerView = findViewById(R.id.recyclerViewDenuncias)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Recupera o idCliente do SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val id = sharedPreferences.getLong("idUsuario", 0L)


        Log.e("Activity", "ID do cliente recebido: $id")

        if (id != 0L) {
            // Busca as denúncias no ViewModel
            viewModel.getDenuncias(id)
        } else {
            Log.e("Activity", "ID do cliente não encontrado no SharedPreferences.")
        }

        // Função de excluir que será passada para o Adapter
        // Função de exclusão que será passada para o Adapter
        val onExcluirClick: (Long) -> Unit = { idDenuncia ->
            excluirDenuncia(idDenuncia)
        }


        // Função de atualização que será passada para o Adapter
        val onAtualizarClick: (Long, String, String) -> Unit = { idDenuncia, tipoDenuncia, descricao ->

            // Lógica para atualizar a denúncia
            viewModel.editarDenuncia(idDenuncia, tipoDenuncia, descricao)
        }

        // Configura o Adapter para o RecyclerView e passa as funções de exclusão e atualização
        denunciaAdapter = DenunciaAdapter(onExcluirClick, onAtualizarClick)
        recyclerView.adapter = denunciaAdapter

        viewModel.denuncias.observe(this, Observer { denuncias ->
            if (denuncias.isNotEmpty()) {
                // Aqui estamos passando a lista de denúncias para o Adapter
                denunciaAdapter.submitList(denuncias)
                Log.e("Activity", "Lista de denúncias recebida: $denuncias")
            } else {
                Log.e("Activity", "Lista de denúncias está vazia.")
            }
        })

    }

    // Função de exclusão da denúncia
    private fun excluirDenuncia(idDenuncia: Long) {
        Log.e("Activity", "Excluindo a denúncia com ID: $idDenuncia")
        // Aqui você pode chamar o ViewModel ou API para excluir a denúncia
        viewModel.deletarDenuncia(idDenuncia)
    }
}
