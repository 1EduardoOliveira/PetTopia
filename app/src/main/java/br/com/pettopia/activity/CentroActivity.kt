package br.com.pettopia.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.pettopia.EsqueciSenhaActivity
import br.com.pettopia.R
import br.com.pettopia.databinding.ActivityCentroBinding

class CentroActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCentroBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        binding.nomepessoa.setText(
            sharedPreferences.getString(
                "nomeUsuario",
                "Nome não encontrado"
            )
        )
        binding.emailpessoa.setText(
            sharedPreferences.getString(
                "emailUsuario",
                "Email não encontrado"
            )
        )

        binding.DenunciasBtt.setOnClickListener {
            val intent = Intent(this@CentroActivity, CaminhoDenunciaActivity::class.java)
            startActivity(intent)
        }

        binding.AdotarAnimalBtt.setOnClickListener {
            val intent = Intent(this@CentroActivity, AdotarAnimaisActivity::class.java)
            startActivity(intent)
        }

        binding.ComprovantesBtt.setOnClickListener {
            val intent = Intent(this@CentroActivity, ComprovantesActivity::class.java)
            startActivity(intent)
        }

        binding.DoarAnimalBtt.setOnClickListener {
            val intent = Intent(this@CentroActivity, DoarAnimalActivity::class.java)
            startActivity(intent)
        }

        binding.SolicitacoesBtt.setOnClickListener {
            val intent = Intent(this@CentroActivity, SolicitacoesUsuarioActivity::class.java)
            startActivity(intent)
        }
        binding.EditarPerfilBtt.setOnClickListener {
            val intent = Intent(this@CentroActivity, EditarPerfilActivity::class.java)
            startActivity(intent)
        }
    }
}
