package br.com.pettopia

import android.icu.util.Calendar
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.AdapterView
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import br.com.pettopia.Enum.generoCliente
import br.com.pettopia.activity.LoginActivity
import br.com.pettopia.databinding.ActivityCadastroUsuarioBinding
import br.com.pettopia.model.ClienteModel
import br.com.pettopia.viewModel.ClienteViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CadastroUsuarioActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityCadastroUsuarioBinding.inflate(layoutInflater)
    }

    private val clienteViewModel: ClienteViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val items = listOf("MASCULINO", "FEMININO", "OUTROS")
        val autoComplete: AutoCompleteTextView = findViewById(R.id.auto_complete_txt)
        val adapter = ArrayAdapter<String>(this, R.layout.list_item, items)

//        val adapter = ArrayAdapter(this, R.layout.list_item, items)
        autoComplete.setAdapter(adapter)

        autoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->

                val itemSelected = adapterView.getItemAtPosition(i)
                Toast.makeText(this, "Item:: $itemSelected", Toast.LENGTH_SHORT).show()
            }

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
                    val formatted = maskTelefone(cleanString) // Aplica a máscara ao número
                    if (formatted != current) {
                        current = formatted
                        binding.tieTelefone.setText(formatted)
                        binding.tieTelefone.setSelection(formatted.length) // Move o cursor para o final
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.tieDataNascimento.setOnClickListener {
            showDatePickerDialog()
        }

        binding.CadastrarBtt.setOnClickListener {

            val dataNascimentoString = binding.tieDataNascimento.text.toString()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy") // Defina o padrão

            if (validarCampos()) {
                val cpf = binding.tieCpf.text.toString()
                val nome = binding.tieNome.text.toString()
                val email = binding.tieEmail.text.toString()
                val password = binding.tieSenha.text.toString()
                val telefone = binding.tieTelefone.text.toString()
                val genero = autoComplete.text

                // Parse e formate a data
                val dataNascimento = LocalDate.parse(dataNascimentoString, formatter)

                // Formate a data para o formato ISO (yyyy-MM-dd)
                val dataNascimentoFormatted =
                    dataNascimento.format(DateTimeFormatter.ISO_LOCAL_DATE)

                // Crie o modelo com o formato correto
                val cliente = ClienteModel(
                    cpf = cpf,
                    nome = nome,
                    email = email,
                    passwordCliente = password,
                    telefone = telefone,
                    generoCliente = generoCliente.valueOf(genero.toString()),
                    dataNascimento = dataNascimento.toString() // Enviar como String formatada
                )
                clienteViewModel.cadastrarCliente(cliente) { response ->
                    if (response.isSuccessful) {
                        val dataNascimentoStr = cliente.dataNascimento?.toString() ?: "Data não disponível"
                        sharedPreferences.edit().apply {
                            cliente?.idCliente?.let { it1 -> putLong("idUsuario", it1) }
                            putString("nomeUsuario", cliente?.nome)  // Nome do usuário
                            putString("emailUsuario", cliente?.email)  // Email do usuário
                            putString("cpfUsuario", cliente?.cpf)  // CPF do usuário
                            putString("dataNascUsuario", dataNascimentoStr)
                            putString("telefoneUsuario", cliente?.telefone)  // Telefone do usuário
                            apply() // Confirma a gravação
                        }

                        // Cadastro bem-sucedido
                        Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        // Erro no cadastro
                        Toast.makeText(
                            this,
                            "Erro no cadastro: ${response.message()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this, "Por favor, corrija os erros.", Toast.LENGTH_SHORT)
                    .show()
            }


        }
    }

    private fun validarCampos(): Boolean {
        val nome = binding.tieNome.text.toString()
        val email = binding.tieEmail.text.toString()
        val senha = binding.tieSenha.text.toString()
        val confirmarSenha = binding.tieConfirmarSenha.text.toString()
        val cpf = binding.tieCpf.text.toString()
        val telefone = binding.tieTelefone.text.toString()
        val dataNascimento = binding.tieDataNascimento.text.toString()
        val genero = binding.autoCompleteTxt.text.toString()

        var isValido = true

        // Validação do Nome
        if (nome.isNullOrEmpty()) {
            binding.tilNome.error = "O nome não pode estar vazio."
            isValido = false
        } else if (nome.length < 3) {
            binding.tilNome.error = "O nome deve ter pelo menos 3 caracteres."
            isValido = false
        } else {
            binding.tilNome.error = null
        }

        // Validação do Email
        if (email.isNullOrEmpty()) {
            binding.tilEmail.error = "O email não pode estar vazio."
            isValido = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Email inválido. Verifique o formato."
            isValido = false
        } else {
            binding.tilEmail.error = null
        }

        // Validação da Senha
        if (senha.isNullOrEmpty()) {
            binding.tilSenha.error = "A senha não pode estar vazia."
            isValido = false
        } else if (senha.length < 6) {
            binding.tilSenha.error = "A senha deve ter pelo menos 6 caracteres."
            isValido = false
        } else if (senha.length > 12) {
            binding.tilSenha.error = "A senha deve ter no máximo 12 caracteres."
            isValido = false
        } else if (!containsSpecialCharacter(senha)) {
            binding.tilSenha.error = "A senha deve incluir pelo menos um caractere especial."
            isValido = false
        } else {
            binding.tilSenha.error = null
        }

        // Validação da Confirmação da Senha
        if (confirmarSenha.isNullOrEmpty()) {
            binding.tilConfirmarSenha.error = "Por favor, confirme sua senha."
            isValido = false
        } else if (senha != confirmarSenha) {
            binding.tilConfirmarSenha.error = "As senhas não coincidem."
            isValido = false
        } else {
            binding.tilConfirmarSenha.error = null
        }

        // Validação do CPF
        if (cpf.isEmpty() || !isCpfValid(cpf)) {
            binding.tilCpf.error = "CPF inválido. Verifique e tente novamente."
            isValido = false
        } else {
            binding.tilCpf.error = null
        }

        // Validação do Telefone
        if (!isTelefoneValid(telefone)) {
            binding.tilTelefone.error = "Telefone inválido. Verifique o formato."
            isValido = false
        } else if (telefone.isEmpty()) {
            binding.tilTelefone.error = "Telefone não pode estar vazio."
        } else {
            binding.tilTelefone.error = null
        }

        // Validação da Data de Nascimento
        if (dataNascimento.isEmpty()) {
            binding.tilDataNascimento.error = "Data de nascimento não pode estar vazia."
            isValido = false
        } else {
            if (!isDataNascimentoValida(dataNascimento)) {
                binding.tilDataNascimento.error = "Data inválida."
                isValido = false
            } else if (calcularIdade(dataNascimento) < 18) {
                binding.tilDataNascimento.error = "Você deve ter pelo menos 18 anos."
                isValido = false
            } else {
                binding.tilDataNascimento.error = null
            }

        }

        // Validação do Gênero
        if (genero.isEmpty()) {
            binding.tilGenero.error = "Gênero não pode estar vazio."
            isValido = false
        } else {
            binding.tilGenero.error = null
        }
        if (!binding.checkTermos.isChecked) {
            Toast.makeText(this, "Você deve concordar com os termos de uso.", Toast.LENGTH_SHORT)
                .show()
            return false
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

    private fun isTelefoneValid(telefone: String): Boolean {
        // Remove caracteres não numéricos
        val cleanTelefone = telefone.replace(Regex("[^\\d]"), "")
        // Valida se o número tem 10 ou 11 dígitos (considerando DDD)
        return cleanTelefone.length == 10 || cleanTelefone.length == 11
    }

    private fun containsSpecialCharacter(password: String): Boolean {
        // Define uma regex para caracteres especiais
        val specialCharacters = "[!@#$%^&*(),.?\":{}|<>/_]"
        return password.contains(Regex(specialCharacters))
    }

    private fun showDatePickerDialog() {
        val calendar = java.util.Calendar.getInstance()
        val anoCalendario = calendar.get(java.util.Calendar.YEAR)
        val mesCalendario = calendar.get(java.util.Calendar.MONTH)
        val diaCalendario = calendar.get(java.util.Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, ano, mes, diaDoMes ->
            val dataFormatada = String.format("%02d/%02d/%04d", diaDoMes, mes + 1, ano)
            binding.tieDataNascimento.setText(dataFormatada)
        }, anoCalendario, mesCalendario, diaCalendario).show()

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
        val calendario = Calendar.getInstance()
        calendario.set(ano, mes - 1, dia) // O mês é baseado em zero

        val dataValida = dia == calendario.get(Calendar.DAY_OF_MONTH) &&
                mes == calendario.get(Calendar.MONTH) + 1 &&
                ano == calendario.get(Calendar.YEAR)

        // Verifica se a data não é uma data futura e se a pessoa tem pelo menos 18 anos
        val idade = Calendar.getInstance().get(Calendar.YEAR) - ano
        return dataValida && idade >= 18
    }

    private fun calcularIdade(dataNascimento: String): Int {
        val partes = dataNascimento.split("/")
        val ano = partes[2].toInt()
        val mes = partes[1].toInt()
        val dia = partes[0].toInt()

        val calendario = Calendar.getInstance()
        var idade = calendario.get(Calendar.YEAR) - ano

        if (calendario.get(Calendar.MONTH) + 1 < mes ||
            (calendario.get(Calendar.MONTH) + 1 == mes && calendario.get(Calendar.DAY_OF_MONTH) < dia)
        ) {
            idade--
        }

        return idade
    }

}

