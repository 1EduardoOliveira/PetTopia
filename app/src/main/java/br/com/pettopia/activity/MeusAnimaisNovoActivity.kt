package br.com.pettopia.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.pettopia.R
import br.com.pettopia.databinding.ActivityMeusAnimaisNovoBinding

class MeusAnimaisNovoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMeusAnimaisNovoBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}