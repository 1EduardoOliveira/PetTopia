package br.com.pettopia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.com.pettopia.activity.LoginActivity
import br.com.pettopia.activity.VerificarEmailActivity
import br.com.pettopia.databinding.ActivityEsqueciSenhaBinding
import br.com.pettopia.model.ClienteModel
import br.com.pettopia.viewModel.ClienteViewModel

class EsqueciSenhaActivity : AppCompatActivity() {

    private val ClienteViewModel: ClienteViewModel by viewModels()

    private val binding by lazy {
        ActivityEsqueciSenhaBinding.inflate(layoutInflater)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.voltarBtt.setOnClickListener {
            val intent = Intent(this@EsqueciSenhaActivity, LoginActivity::class.java)
            startActivity(intent)
        }

        var validacao = true

        binding.Continuar.setOnClickListener {
//            showProgress()

            val email = binding.tieemail.text.toString()
            Log.d("email", "email: $email")
            // Verificar se o campo está vazio
            if (email.isNullOrEmpty()) {
                binding.tillemail.error = "Preencha o campo com seu e-mail"
            }
            // Verificar se o e-mail é válido
            else if (!isEmailValid(email)) {
                binding.tillemail.error = "E-mail inválido. Verifique e tente novamente"
            }
            // Se tudo estiver correto
            else {
                EnviarEmail()

            }

        }

    }

    fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return email.matches(emailPattern.toRegex())
    }

    private fun EnviarEmail() {

        val email = binding.tieemail.text

        var enviarEmail = ClienteModel(email = email.toString())
        Log.d("email", "email: $enviarEmail")
        ClienteViewModel.esqueciasenha(enviarEmail) { response ->



            if (response.isSuccessful) {
                Log.d("response", "response: $response")
//                hideProgress()
                Toast.makeText(this, "Email enviado com sucesso!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, VerificarEmailActivity::class.java)
                startActivity(intent)
            } else {
                //hideProgress()
                Log.d("response", "responseErro: $response")
                Toast.makeText(this, "Email esta errado ou nao existe", Toast.LENGTH_SHORT).show()
                Log.d("eu" , "vai tomando")
                finish()
            }
        }
    }
    private fun showProgress(){
        findViewById<LinearLayout>(R.id.progress).visibility = View.VISIBLE
    }
    private fun hideProgress(){
        findViewById<LinearLayout>(R.id.progress).visibility = View.GONE
    }
}

