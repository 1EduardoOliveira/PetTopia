package br.com.pettopia.activity


import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.pettopia.CadastroUsuarioActivity
import br.com.pettopia.R
import br.com.pettopia.RedefinirSenhaActivity
import br.com.pettopia.databinding.ActivityEditarPerfilBinding
import br.com.pettopia.model.ClienteModel
import com.google.gson.Gson

class EditarPerfilActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityEditarPerfilBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupTextWatchers()
        carregarDadosDoUsuario()


        binding.RedefinirSenhaBtt.setOnClickListener {
            val intent = Intent(this@EditarPerfilActivity, RedefinirSenhaActivity::class.java)
            startActivity(intent)
        }

        binding.CadastrarBtt.setOnClickListener {

            if (validarCampos()) {
                salvarDadosAtualizados()

                Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
//                finish() // Finaliza a atividade
            } else {
                Toast.makeText(this, "Por favor, corrija os erros.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun carregarDadosDoUsuario() {

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

        binding.tieNome.setText(sharedPreferences.getString("nomeUsuario", "vazio"))
        binding.tieEmail.setText(sharedPreferences.getString("emailUsuario", "vazio"))
        binding.tieCpf.setText(sharedPreferences.getString("cpfUsuario", "00000000000"))
        binding.tieTelefone.setText(sharedPreferences.getString("telefoneUsuario", "00000000000"))

    }
    private fun salvarDadosAtualizados() {

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val nome = binding.tieNome.text.toString().trim()
        val email = binding.tieEmail.text.toString().trim()
        val telefone = binding.tieTelefone.text.toString()

        editor.putString("nomeUsuario", nome)
        editor.putString("emailUsuario", email)
        editor.putString("telefoneUsuario", telefone)
        editor.apply()

        // Log para debug
        val clienteModel = ClienteModel(
            nome = nome,
            email = email,
            telefone = telefone,
            generoCliente = null, // Atualize se necessário
            idCliente = 0L // Atualize se necessário
        )

        val gson = Gson()
        Log.d("EditarPerfil", "Cliente atualizado: ${gson.toJson(clienteModel)}")

        val intent = Intent(this@EditarPerfilActivity, CentroActivity::class.java)
        startActivity(intent)
    }

    private fun setupTextWatchers() {
        binding.tieCpf.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    val cleanString = s.toString().replace(Regex("[^\\d]"), "")
                    val formatted = maskCpf(cleanString)
                    if (formatted != current) {
                        current = formatted
                        binding.tieCpf.setText(formatted)
                        binding.tieCpf.setSelection(formatted.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tieTelefone.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString() != current) {
                    val cleanString = s.toString().replace(Regex("[^\\d]"), "")
                    val formatted = maskTelefone(cleanString)
                    if (formatted != current) {
                        current = formatted
                        binding.tieTelefone.setText(formatted)
                        binding.tieTelefone.setSelection(formatted.length)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


    }

    private fun maskCpf(cpf: String): String {
        val cleanedCpf = cpf.replace(Regex("[^\\d]"), "")
        return when {
            cleanedCpf.length > 11 -> cleanedCpf.substring(0, 11)
            cleanedCpf.length >= 9 -> "${cleanedCpf.substring(0, 3)}.${
                cleanedCpf.substring(
                    3,
                    6
                )
            }.${cleanedCpf.substring(6, 9)}-${cleanedCpf.substring(9)}"

            cleanedCpf.length >= 6 -> "${cleanedCpf.substring(0, 3)}.${
                cleanedCpf.substring(
                    3,
                    6
                )
            }.${cleanedCpf.substring(6)}"

            cleanedCpf.length >= 3 -> "${cleanedCpf.substring(0, 3)}.${cleanedCpf.substring(3)}"
            else -> cleanedCpf
        }
    }

    private fun maskTelefone(telefone: String): String {
        val cleanedTelefone = telefone.replace(Regex("[^\\d]"), "")
        return when {
            cleanedTelefone.length > 11 -> cleanedTelefone.substring(0, 11)
            cleanedTelefone.length >= 7 -> "(${
                cleanedTelefone.substring(
                    0,
                    2
                )
            }) ${cleanedTelefone.substring(2, 7)}-${cleanedTelefone.substring(7)}"

            cleanedTelefone.length >= 2 -> "(${
                cleanedTelefone.substring(
                    0,
                    2
                )
            }) ${cleanedTelefone.substring(2)}"

            else -> cleanedTelefone
        }
    }


    private fun validarCampos(): Boolean {
        val nome = binding.tieNome.text.toString().trim()
        val email = binding.tieEmail.text.toString().trim()
        val cpf = binding.tieCpf.text.toString()
        val telefone = binding.tieTelefone.text.toString()


        var isValido = true

        // Validação do Nome
        if (nome.isEmpty()) {
            binding.tilNome.error = "O nome não pode estar vazio."
            isValido = false
        } else if (nome.length < 3) {
            binding.tilNome.error = "O nome deve ter pelo menos 3 caracteres."
            isValido = false
        } else {
            binding.tilNome.error = null
        }

        // Validação do Email
        if (email.isEmpty()) {
            binding.tilEmail.error = "O email não pode estar vazio."
            isValido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email inválido. Verifique o formato."
            isValido = false
        } else {
            binding.tilEmail.error = null
        }

        // Validação do CPF
        if (cpf.isEmpty() || !isCpfValid(cpf)) {
            binding.tilCpf.error = "CPF inválido. Verifique e tente novamente."
            isValido = false
        } else {
            binding.tilCpf.error = null
        }

        // Validação do Telefone
        if (telefone.isEmpty() || !isTelefoneValid(telefone)) {
            binding.tilTelefone.error = "Telefone inválido. Verifique o formato."
            isValido = false
        } else {
            binding.tilTelefone.error = null
        }

        return isValido
    }

    private fun isCpfValid(cpf: String): Boolean {
        // Remove caracteres não numéricos
        val cleanCpf = cpf.replace(Regex("[^\\d]"), "")
        // Verifica se o CPF tem 11 dígitos
        if (cleanCpf.length != 11 || cleanCpf.all { it == cleanCpf[0] }) {
            return false
        }

        // Calcula os dois dígitos verificadores
        val digits = cleanCpf.substring(0, 9).map { it.toString().toInt() }
        val firstCheckDigit = calculateCheckDigit(digits, 10)
        val secondCheckDigit = calculateCheckDigit(digits + firstCheckDigit, 11)

        // Verifica se os dígitos verificadores estão corretos
        return cleanCpf[9].digitToInt() == firstCheckDigit && cleanCpf[10].digitToInt() == secondCheckDigit
    }

    private fun calculateCheckDigit(digits: List<Int>, weight: Int): Int {
        val sum = digits.withIndex().sumBy { (index, value) -> value * (weight - index) }
        val remainder = sum % 11
        return if (remainder < 2) 0 else 11 - remainder
    }

    private fun isTelefoneValid(telefone: String): Boolean {
        // Remove caracteres não numéricos
        val cleanTelefone = telefone.replace(Regex("[^\\d]"), "")
        // Valida se o número tem 10 ou 11 dígitos (considerando DDD)
        return cleanTelefone.length in 10..11
    }

    private fun isDataNascimentoValida(dataNascimento: String): Boolean {
        // Verifica se a data está no formato DD/MM/AAAA
        val regex = Regex("\\d{2}/\\d{2}/\\d{4}")
        if (!regex.matches(dataNascimento)) {
            return false
        }

        val partes = dataNascimento.split("/")
        val dia = partes[0].toInt()
        val mes = partes[1].toInt()
        val ano = partes[2].toInt()

        // Cria um objeto Calendar para validar a data
        val calendario = java.util.Calendar.getInstance()
        calendario.set(ano, mes - 1, dia) // O mês é baseado em zero

        val dataValida = dia == calendario.get(java.util.Calendar.DAY_OF_MONTH) &&
                mes == calendario.get(java.util.Calendar.MONTH) + 1 &&
                ano == calendario.get(java.util.Calendar.YEAR)

        return dataValida && !calendario.after(java.util.Calendar.getInstance())
    }
}