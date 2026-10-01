package br.com.pettopia.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import br.com.helios.mobile.api.ModuloConectividade
import br.com.pettopia.Enum.TipoDenuncia
import br.com.pettopia.Enum.generoCliente
import br.com.pettopia.R
import br.com.pettopia.databinding.ActivityDenunciaBinding
import br.com.pettopia.model.ClienteModel
import br.com.pettopia.model.DenunciaModel
import br.com.pettopia.model.EnderecoModel
import br.com.pettopia.repository.EnderecoRepository
import br.com.pettopia.viewModel.ClienteViewModel
import br.com.pettopia.viewModel.EnderecoViewModel
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DenunciaActivity : AppCompatActivity() {

    private val enderecoViewModel: EnderecoViewModel by viewModels {
        EnderecoViewModel.EnderecoViewModelFactory(EnderecoRepository(ModuloConectividade.getEnderecoService()))
    }

//    private val viewModel: ViaCepViewModel by viewModels {
//        ViaCepViewModelFactory(ViaCepRepository(ViaCepApi.create()))
//    }
    private val binding by lazy {
        ActivityDenunciaBinding.inflate(layoutInflater)
    }

    override fun onResume() {
        super.onResume()
        hideProgress()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val items = listOf("ABANDONADO", "VIOLENCIA")
        val autoComplete: AutoCompleteTextView = findViewById(R.id.ComboDenuncia)
        val adapter = ArrayAdapter<String>(this, R.layout.list_item, items)
        autoComplete.setAdapter(adapter)

        autoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val itemSelected = adapterView.getItemAtPosition(i)
                Toast.makeText(this, "Item:: $itemSelected", Toast.LENGTH_SHORT).show()
            }

        binding.tieCep.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val cep = s.toString()
                if (cep.length == 8) {
                    // Dispara a busca do endereço quando o CEP tiver 8 caracteres
                    enderecoViewModel.getEndereco(cep)
                    showProgress()
                }
            }
        }
        )

        //CONTINUAR PARA OUTRA ACTIVTY - CAMPOS DENUNCIA
        binding.RealizarDenunciasBtt.setOnClickListener {
            RegistarDenuncia()
        }

        enderecoViewModel.enderecoLiveData.observe(this) { endereco ->
            endereco?.let {

                Log.d("Endereco", "Localidade: ${it.localidade}")
                // Preenche os campos com os dados recebidos da API
                binding.tieLogradouro.setText(it.logradouro)  // Preenche o campo de logradouro
                binding.tieBairro.setText(it.bairro)          // Preenche o campo de bairro
                binding.tieUf.setText(it.uf)  // Preenche o campo de localidade
                binding.tieCidade.setText(it.localidade)          // Preenche o campo de localidade)
                hideProgress()
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun RegistarDenuncia() {
        val clienteViewModel: ClienteViewModel by viewModels()

        // Captura os dados de endereço diretamente dos campos de input no layout
        val rua = binding.tieLogradouro.text.toString()
        val bairro = binding.tieBairro.text.toString()
        val localidade = binding.tieCidade.text.toString()
        val uf = binding.tieUf.text.toString()
        val cep = binding.tieCep.text.toString()
        val tipoDenunciaa = binding.ComboDenuncia.text
        val descricao = binding.tieDescricao.text.toString()



        // Salvando os dados de endereço no SharedPreferences
        val sharedPreferences = applicationContext.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("rua", rua)
        editor.putString("bairro", bairro)
        editor.putString("localidade", localidade)
        editor.putString("tipoDenuncia", tipoDenunciaa.toString())
        editor.putString("descricaoDenuncia", descricao)
        editor.putString("uf", uf)
        editor.putString("cep", cep)
        editor.apply()

        // Obtendo os dados de SharedPreferences para o Cliente
        val nomeUsuario = sharedPreferences.getString("nomeUsuario", "Nome não disponível")
        val email = sharedPreferences.getString("emailUsuario", "Email não disponível")
        val senha = sharedPreferences.getString("senhaUsuario", "Senha não disponível")
        val telefone = sharedPreferences.getString("telefoneUsuario", "Telefone não disponível")
        val generoClienteStr = sharedPreferences.getString("generoUsuario", null)
        val dataNascimentoString = sharedPreferences.getString("dataNascUsuario", "Data não disponível")
        val cpf = sharedPreferences.getString("cpfUsuario", "CPF não disponível")
        val id = sharedPreferences.getLong("idUsuario", 0L)
        val token = sharedPreferences.getString("authToken", "Token não disponível")

        //Conversao do generoCliente de string para ENUM
        val generoCliente = generoClienteStr?.let { generoCliente.valueOf(it)
            try {
                generoCliente.valueOf(it)
            } catch (e: IllegalArgumentException) {
                null // Retorna null se o valor não corresponder a um enum válido
            }
        }


        fun String.toLocalDate(): LocalDate? {
            return if (this == "Data não disponível" || this.isEmpty()) {
                null // Retorna null se a string for inválida
            } else {
                try {
                    LocalDate.parse(this, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                } catch (e: Exception) {
                    null // Retorna null se ocorrer algum erro na conversão
                }
            }
        }

// Recuperando do SharedPreferences

        val dataNascimento = dataNascimentoString?.toLocalDate()

//        val dataNascimento = if (dataNascString != "Data não disponível" && dataNascString != null) {
//            try {
//                // Tentando converter a string para LocalDate usando o formato ISO (yyyy-MM-dd)
//                LocalDate.parse(dataNascString)
//            } catch (e: Exception) {
//                // Se ocorrer erro na conversão, podemos logar ou usar um valor padrão
//                Log.e("SharedPreferences", "Erro ao converter data de nascimento", e)
//                null  // Retornando null caso a conversão falhe
//            }
//        } else {
//            null  // Retornando null se a data não estiver disponível
//        }


        // Criando o modelo Cliente
        Log.d("SharedPreferences", "Dados recuperados: Nome: $nomeUsuario, Email: $email")
        val cliente = ClienteModel(
            email = email ?: "Email não disponível",
            passwordCliente = senha ?: "Senha não disponível",
            telefone = telefone ?: "Telefone não disponível",
            generoCliente = generoCliente,
            cpf = cpf ?: "CPF não disponível",
            nome = nomeUsuario ?: "Nome não disponível",
            idCliente = id ?: 0L
        )


        // Criando o modelo Endereço com os dados capturados
        val endereco = EnderecoModel(
            logradouro = rua ?: "Rua não disponível",
            numero = 0L,  // Não foi fornecido campo de número no método
            bairro = bairro ?: "Bairro não disponível",
            uf = uf ?: "UF não disponível",
            localidade = localidade?: "Estado não disponível",
            cep = cep
        )



        // Criando a Denúncia
        val descricaoDenuncia = descricao
        val statusDenuncia = "PENDENTE"

        Log.d("Denuncia", "Cliente: $cliente")
        Log.d("Denuncia", "Endereço: $endereco")




        val denuncia = DenunciaModel(
            cliente = cliente,
            endereco = endereco, // Transformação do endereço em string
            descricao = descricaoDenuncia ?: "Descrição não disponível",
            tipoDenucias = tipoDenunciaa.toString(),
            statusGeral = statusDenuncia
        )

        val gson = Gson()
        val denunciaJson = gson.toJson(denuncia)
        Log.d("Denuncia", "JSON Enviado: $denunciaJson")

        // Realizando a denúncia através do ViewModel
        clienteViewModel.realizarDenuncia(denuncia) { response ->
            Log.d("Denuncia", "Resposta da requisição: ${response.isSuccessful}")
            if (response.isSuccessful) {
                Toast.makeText(this,"coisa ${id}", Toast.LENGTH_SHORT).show()
//                Toast.makeText(this, "Denúncia registrada com sucesso!", Toast.LENGTH_SHORT).show()
                // Salvar a denúncia no SharedPreferences (se necessário)
//                sharedPreferences.edit().apply {
//                    putString("denuncia", denuncia.toString())
//                    apply()
//                }
                // Redireciona o usuário para outra Activity (HomeActivity no seu caso)

                val intent = Intent(this, CentroActivity::class.java)
                startActivity(intent)

            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("Denuncia", "Erro ao registrar a denúncia: ${response.code()} - $errorBody")

                Log.e("Denuncia", "Erro ao registrar a denúncia: ${response.message()}")
                Toast.makeText(this, "Erro ao registrar a denúncia!", Toast.LENGTH_SHORT).show()
                finish()
            }

            // Log de sucesso
            Log.d("Denuncia", "Denúncia criada com sucesso: $denuncia")
            Log.d("Denuncia", "Cliente: $cliente")
            Log.d("Denuncia", "Endereço: $endereco")
        }
    }
    private fun showProgress(){
        findViewById<LinearLayout>(R.id.progress).visibility = View.VISIBLE
    }
    private fun hideProgress(){
        findViewById<LinearLayout>(R.id.progress).visibility = View.GONE
    }
}