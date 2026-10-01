package br.com.pettopia.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.pettopia.CadastroUsuarioActivity
import br.com.pettopia.EsqueciSenhaActivity
import br.com.pettopia.databinding.ActivityLoginBinding
import br.com.pettopia.model.LoginRequest
import br.com.pettopia.viewModel.ClienteViewModel
import com.google.android.material.snackbar.Snackbar

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val clienteViewModel: ClienteViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPreferences = applicationContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        // Recuperar o estado de "Relembra-me"
        val isRemembered = sharedPreferences.getBoolean("isRemembered", false)
        val savedEmail = sharedPreferences.getString("email", null)

        if (isRemembered && savedEmail != null) {
            // Ação de login automática
            loginUser(savedEmail)
        }

        binding.EsqueciSenha.setOnClickListener {
            val intent = Intent(this@LoginActivity, EsqueciSenhaActivity::class.java)
            startActivity(intent)
        }

        binding.criarConta.setOnClickListener {
            startActivity(Intent(this, CadastroUsuarioActivity::class.java))
        }

        binding.EntrarBtt.setOnClickListener {
            if (validarCampos()) {
                val email = binding.tieemail.text.toString().trim()
                val senha = binding.tiesenha.text.toString().trim()
                val rememberMe = binding.lembrar.isChecked

                // Exibe Snackbar para feedback imediato (será atualizado depois do login)
                Snackbar.make(binding.root, "Tentando realizar login...", Snackbar.LENGTH_SHORT).show()

                // Lógica de "Relembra-me"
                with(sharedPreferences.edit()) {
                    if (rememberMe) {
                        putBoolean("isRemembered", true)
                        putString("email", email)
                    } else {
                        remove("email")
                        putBoolean("isRemembered", false)
                    }
                    apply()
                }

                // Criação do objeto LoginRequest
                val cliente = LoginRequest(
                    email = email,
                    passwordCliente = senha
                )

                // Chama o ViewModel para processar o login
                clienteViewModel.loginCliente(cliente) { response ->
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        val token = authResponse?.token
                        val cliente = authResponse?.cliente
                        val dataNascimentoStr = cliente?.dataNascimento?.toString() ?: "Data não disponível"

                        sharedPreferences.edit().apply {

                            putString("authToken", token)  // Exemplo de token, pode ser necessário ajustar conforme o seu caso
                            putString("nomeUsuario", cliente?.nome)  // Nome do usuário
                            putString("emailUsuario", cliente?.email)  // Email do usuário
                            putString("cpfUsuario", cliente?.cpf)  // CPF do usuário
                            putString("dataNascUsuario", dataNascimentoStr)
                            putString("telefoneUsuario", cliente?.telefone)  // Telefone do usuário
                            putString("generoUsuario", cliente?.generoCliente?.name)  // Gênero do usuário
                            putString("senhaUsuario", cliente?.passwordCliente)  // Senha do usuário")
                            cliente?.idCliente?.let { it1 -> putLong("idUsuario", it1) }  // Gênero do usuário")
                            apply()  // Salva de forma assíncrona, mais recomendado
                        }
                        Log.d("Login Success", "Login realizado com sucesso")
                        if (cliente != null) {
                            Log.d("SharedPreferences", "data: ${cliente.dataNascimento}, genero: ${cliente.generoCliente}")
                        }
                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()

                        Log.d("Login Success", "Login realizado com sucesso, idCliente: ${cliente?.idCliente}")


                        // Navega para HomeActivity e finaliza a LoginActivity
                        val intent = Intent(this, CentroActivity::class.java)

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.putExtra("nomeUsuario", cliente?.nome)  // Passando o nome do cliente
                        intent.putExtra("emailUsuario", cliente?.email)
                        intent.putExtra("cpfUsuario", cliente?.cpf)
                        intent.putExtra("dataNascUsuario", dataNascimentoStr)
                        intent.putExtra("telefoneUsuario", cliente?.telefone)
                        intent.putExtra("generoUsuario", cliente?.generoCliente)
                        startActivity(intent)
                        finish()

                    } else if (response.code() == 401) {
                        // Caso de credenciais inválidas
                        Log.e("Login Error", "Credenciais inválidas")
                        Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show()
                    } else {
                        // Tratamento para outros erros
                        Log.e("Login Error", "Erro no login: ${response.message()}")
                        Toast.makeText(this, "Erro no login: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Snackbar.make(binding.root, "Por favor, corrija os erros.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    // Método de validação dos campos
    private fun validarCampos(): Boolean {
        var isValid = true

        // Validação do Email
        val email = binding.tieemail.text.toString().trim()
        if (email.isEmpty()) {
            mostrarSnackbarErro("O campo Email é obrigatório.")
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mostrarSnackbarErro("Formato de email inválido.")
            isValid = false
        }

        // Validação da Senha
        val senha = binding.tiesenha.text.toString().trim()
        if (senha.isEmpty()) {
            mostrarSnackbarErro("O campo Senha é obrigatório.")
            isValid = false
        } else if (senha.length < 4) {
            mostrarSnackbarErro("A senha deve ter no mínimo 6 caracteres.")
            isValid = false
        }

        return isValid
    }

    // Função para exibir o Snackbar de erro
    private fun mostrarSnackbarErro(mensagem: String) {
        Snackbar.make(binding.root, mensagem, Snackbar.LENGTH_LONG).apply {
            setAction("OK") { dismiss() } // Ação para fechar o Snackbar
            setBackgroundTint(
                resources.getColor(
                    android.R.color.holo_red_light,
                    null
                )
            ) // Cor de fundo
            setTextColor(resources.getColor(android.R.color.white, null)) // Cor do texto
        }.show()
    }

    // Método para simular o login automático
    private fun loginUser(email: String) {
        // Lógica para redirecionar o usuário para a HomeActivity
        startActivity(Intent(this, CentroActivity::class.java))
        finish()
        Snackbar.make(binding.root, "Login automático para: $email", Snackbar.LENGTH_SHORT).show()
    }
}