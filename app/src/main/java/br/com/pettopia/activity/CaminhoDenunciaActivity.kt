package br.com.pettopia.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.pettopia.R
import br.com.pettopia.databinding.ActivityCaminhoDenunciaBinding

class CaminhoDenunciaActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityCaminhoDenunciaBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.bttVisDenuncia.setOnClickListener {
            val intent = Intent(this, VisualizarDenunciaActivity::class.java)
            startActivity(intent)
        }
        binding.bttCadDenuncia.setOnClickListener {
            val intent = Intent(this, DenunciaActivity::class.java)
            startActivity(intent)
        }
    }
}