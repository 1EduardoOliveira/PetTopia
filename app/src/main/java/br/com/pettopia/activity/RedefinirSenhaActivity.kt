package br.com.pettopia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.pettopia.activity.VerificarEmailActivity
import br.com.pettopia.databinding.ActivityRedefinirSenhaBinding
import br.com.pettopia.model.ClienteModel
import br.com.pettopia.model.ValidarToken
import br.com.pettopia.viewModel.ClienteViewModel

class RedefinirSenhaActivity : AppCompatActivity() {

    private val ClienteViewModel: ClienteViewModel by viewModels()

    private val binding by lazy {
        ActivityRedefinirSenhaBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnConfirmar.setOnClickListener {
            val intent = Intent(this@RedefinirSenhaActivity, VerificarEmailActivity::class.java)
            startActivity(intent)
        }
        val isValido = true

        binding.btnConfirmar.setOnClickListener {

            if (binding.tieSenha.text.toString() != binding.tieConfirmarSenha.text.toString()) {
                binding.tilConfirmarSenha.error = "Senhas Não conferem"
                binding.tieSenha.error = "Senhas Não conferem"
            } else {
                redefinirsenha()
            }
        }
    }

    private fun redefinirsenha() {

        val senhaAtualizada = binding.tieConfirmarSenha.text.toString()

        val sharedPreferences =
            applicationContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val emailCliente = sharedPreferences.getString("emailUsuario", "nao existente")

        val clienteModel = ClienteModel(email = emailCliente, passwordCliente = senhaAtualizada)

        // Chamar o método do ViewModel
        ClienteViewModel.atualizarsenha(clienteModel) { response ->

            if (response.isSuccessful) {
                val intent = Intent(this, RedefinirSenhaActivity::class.java)
                startActivity(intent)

            } else {
                Toast.makeText(this, "token esta errado ou nao existe", Toast.LENGTH_SHORT).show()
                Log.d("eu", "senha trocada fudido")
                finish()
            }
        }
    }
}
