package br.com.pettopia.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.pettopia.CadastroUsuarioActivity
import br.com.pettopia.databinding.ActivitySaudacoesBinding

class SaudacoesActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivitySaudacoesBinding.inflate(layoutInflater)
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Verifica se o usuário foi lembrado
        val isRemembered = sharedPreferences.getBoolean("isRemembered", false)

        if (isRemembered) {
            // Se o usuário foi lembrado, redireciona para a HomeActivity
            startActivity(Intent(this, CentroActivity::class.java))
            finish() // Finaliza a SaudacoesActivity
            return // Para garantir que o restante do onCreate não seja executado
        }

        binding.BttLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.BttCadastro.setOnClickListener {
            startActivity(Intent(this, CadastroUsuarioActivity::class.java))
        }
    }
}