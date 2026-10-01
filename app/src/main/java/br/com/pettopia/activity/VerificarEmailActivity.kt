package br.com.pettopia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.pettopia.RedefinirSenhaActivity
import br.com.pettopia.databinding.ActivityVerificarEmailBinding
import br.com.pettopia.model.ValidarToken
import br.com.pettopia.viewModel.ClienteViewModel
import retrofit2.Response

class VerificarEmailActivity : AppCompatActivity() {

    private val ClienteViewModel: ClienteViewModel by viewModels()

    private val binding by lazy {
        ActivityVerificarEmailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.btnVoltar.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnVerificar.setOnClickListener {

            val token = listOf(
                binding.etCode1.text.toString(),
                binding.etCode2.text.toString(),
                binding.etCode3.text.toString(),
                binding.etCode4.text.toString(),
                binding.etCode5.text.toString(),
                binding.etCode6.text.toString()
            ).joinToString(separator = "")

            Log.d("charles manson", "token: $token")

            val sharedPreferences =
                applicationContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val emailCliente = sharedPreferences.getString("emailUsuario", "nao existente")
            Log.d("email", "email: $emailCliente")
            val validarToken = ValidarToken(
                token = token.toString(),
                email = emailCliente.toString()
            )
            Log.d("token", "JSON: $validarToken")

            // Chamar o método do ViewModel
            ClienteViewModel.validartoken(validarToken) { response ->

                if (response.isSuccessful) {
                    Log.d("API Response", "Resposta da API: ${response.body()}")
                    Log.d("API Response", "Erro na API: ${response.errorBody()?.string()}")
                    val intent = Intent(this, RedefinirSenhaActivity::class.java)
                    startActivity(intent)

                } else {
                    Toast.makeText(this, "token esta errado ou nao existe", Toast.LENGTH_SHORT)
                        .show()
                    Log.d("eu", "token fudido")
                    finish()
                }
            }
        }

        // Configura os TextWatchers para os campos de código
        setupTextWatchers()
    }

    /**
     * Configura os TextWatchers para os campos de entrada do código.
     * Os campos são configurados para navegar automaticamente entre si,
     * dependendo da entrada do usuário.
     */
    private fun setupTextWatchers() {
        // Lista de campos de entrada de código
        val editTexts = listOf(
            binding.etCode1,
            binding.etCode2,
            binding.etCode3,
            binding.etCode4,
            binding.etCode5,
            binding.etCode6
        )

        // Itera sobre cada campo de entrada e configura os TextWatchers
        for (i in editTexts.indices) {
            editTexts[i].addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    // Não faz nada antes que o texto mude
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    // Mover para o próximo campo se houver texto digitado
                    if (s.toString().length == 1 && i < editTexts.size - 1) {
                        editTexts[i + 1].requestFocus()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    // Se o campo estiver vazio, mover para o campo anterior
                    if (s.toString().isEmpty() && i > 0) {
                        editTexts[i - 1].requestFocus()
                    }
                }
            })

            // Listener para detectar a remoção de caracteres
            editTexts[i].setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    // Mover para o campo anterior ao apagar o dígito
                    editTexts[i].addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {
                            // Não faz nada antes que o texto mude
                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            // Se o usuário apagou um caractere
                            if (before > 0 && count == 0) {
                                // Mover para o campo anterior
                                if (i > 0) {
                                    editTexts[i - 1].requestFocus()
                                }
                            }
                        }

                        override fun afterTextChanged(s: Editable?) {
                            // Não faz nada após a alteração do texto
                        }
                    })
                }
            }
        }
    }

}
