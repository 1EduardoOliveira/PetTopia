package br.com.pettopia.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.com.pettopia.databinding.ActivityCarregamentoBinding


class CarregamentoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCarregamentoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        android.os.Handler().postDelayed({
            val intent = Intent(this@CarregamentoActivity, SaudacoesActivity::class.java)
            startActivity(intent)
            finish()
        }, 3200)
    }

}
