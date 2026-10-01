package br.com.pettopia.activity

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import br.com.pettopia.R
import br.com.pettopia.databinding.ActivityDoarAnimalBinding
import br.com.pettopia.model.AnimalModel
import br.com.pettopia.model.ClienteModel
import br.com.pettopia.viewModel.ClienteViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DoarAnimalActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityDoarAnimalBinding.inflate(layoutInflater)
    }

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>
    private var fotoUri: Uri? = null
    private var fotoSelecionada: File? = null
    private val speciesToBreeds = mapOf(
        "CACHORRO" to listOf("PUG", "BULDOGUE", "SALSICHA", "PASTOR_ALEMAO","ROTTWEILER","LABRADROR","PINSCHER","PITTBULL","BULL_TERRIR"),
        "GATO" to listOf("SIAMES", "PERSA", "MAINE_COON", "AMERICAN")
    )

    private val colorList =
        listOf("Preto", "Branco", "Marrom", "Cinza", "Amarelo", "Verde", "Azul", "Vermelho")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupSpeciesDropdown()
        setupAnimalDropdown()
        setupAgeDropdown()
        setupRaceDropdown() // Inicializa o dropdown de raças
        setupColorDropdown() // Inicializa o dropdown de cores
        setupGalleryLauncher()

        // Configura o listener de clique para o campo de texto da foto
        binding.tieFoto.setOnClickListener {
            openGallery() // Abre a galeria quando o campo de texto é clicado
        }
        binding.CadastrarBtt.setOnClickListener {
            // metodos
            cadastrarAnimal()
        }
    }

    // Configura o dropdown para seleção de espécie
    private fun setupSpeciesDropdown() {
        val items = listOf("CACHORRO", "GATO") // Lista de opções
        val autoComplete: AutoCompleteTextView =
            binding.ComboEspecie // Obtém o AutoCompleteTextView
        val adapter = ArrayAdapter(this, R.layout.list_item, items) // Adapter para as opções
        autoComplete.setAdapter(adapter) // Configura o adapter no AutoCompleteTextView

        // Listener para ação ao selecionar um item
        autoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->
                val itemSelected = adapterView.getItemAtPosition(i) as String // Item selecionado
                Toast.makeText(this, "Espécie: $itemSelected", Toast.LENGTH_SHORT)
                    .show() // Exibe o item selecionado
                updateRaceDropdown(itemSelected) // Atualiza as raças com base na espécie selecionada
            }
    }

    private fun setupAnimalDropdown() {
        val items = listOf("MACHO", "FEMEA") // Lista de opções
        val autoComplete: AutoCompleteTextView = binding.ComboSexo // Obtém o AutoCompleteTextView
        val adapter = ArrayAdapter(this, R.layout.list_item, items) // Adapter para as opções
        autoComplete.setAdapter(adapter) // Configura o adapter no AutoCompleteTextView

        // Listener para ação ao selecionar um item
        autoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->
                val itemSelected = adapterView.getItemAtPosition(i) // Item selecionado
                Toast.makeText(this, "Sexo: $itemSelected", Toast.LENGTH_SHORT)
                    .show() // Exibe o item selecionado
            }
    }

    private fun setupRaceDropdown() {
        val autoComplete: AutoCompleteTextView = binding.ComboRaca // Obtém o AutoCompleteTextView
        autoComplete.setAdapter(
            ArrayAdapter<String>(
                this,
                R.layout.list_item,
                emptyList()
            )
        ) // Inicializa com lista vazia
        autoComplete.setOnClickListener {
            val selectedSpecies =
                binding.ComboEspecie.text.toString() // Obtém a espécie selecionada
            if (selectedSpecies.isEmpty()) { // Verifica se a espécie foi selecionada
                Toast.makeText(
                    this,
                    "Por favor, selecione a espécie do animal primeiro.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setupColorDropdown() {
        val autoComplete: AutoCompleteTextView =
            binding.ComboCor // Obtém o AutoCompleteTextView para a cor
        val adapter = ArrayAdapter(this, R.layout.list_item, colorList) // Adapter para as cores
        autoComplete.setAdapter(adapter) // Configura o adapter

        // Listener para ação ao selecionar um item
        autoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->
                val itemSelected = adapterView.getItemAtPosition(i) as String // Item selecionado
                Toast.makeText(this, "Cor selecionada: $itemSelected", Toast.LENGTH_SHORT)
                    .show() // Exibe o item selecionado
            }
    }

    private fun setupAgeDropdown() {
        val items = listOf(
            "ZERO_A_TRES_MESES",
            "3 a 4 anos",
            "5 a 6 anos",
            "7 a 8 anos",
            "9 a 10 anos"
        ) // Lista de opções
        val autoComplete: AutoCompleteTextView =
            binding.ComboIdade // Obtém o AutoCompleteTextView
        val adapter = ArrayAdapter(this, R.layout.list_item, items) // Adapter para as opções
        autoComplete.setAdapter(adapter) // Configura o adapter no AutoCompleteTextView

        // Listener para ação ao selecionar um item
        autoComplete.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, _, i, _ ->
                val itemSelected = adapterView.getItemAtPosition(i) // Item selecionado
                Toast.makeText(this, "Idade: $itemSelected", Toast.LENGTH_SHORT)
                    .show() // Exibe o item selecionado
            }
    }

    // Método para atualizar o dropdown de raças com base na espécie selecionada
    private fun updateRaceDropdown(selectedSpecies: String) {
        val breeds = speciesToBreeds[selectedSpecies]
            ?: emptyList() // Obtém as raças para a espécie selecionada
        val autoComplete: AutoCompleteTextView = binding.ComboRaca
        val adapter = ArrayAdapter(this, R.layout.list_item, breeds)
        autoComplete.setAdapter(adapter)
        autoComplete.setText("", false) // Limpa o campo de texto da raça
    }

    // Configura o launcher para abrir a galeria
    private fun setupGalleryLauncher() {
        galleryLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                // Verifica se o resultado foi OK
                if (result.resultCode == RESULT_OK) {
                    result.data?.data?.let { uri ->
                        fotoUri = uri
                        fotoSelecionada = uriToFile(uri)
                        displayFileName(uri) // Exibe o nome do arquivo se a URI não for nula
                    } ?: run {
                        Toast.makeText(this, "Erro ao obter a imagem.", Toast.LENGTH_SHORT)
                            .show() // Exibe erro se a URI for nula
                    }
                }
            }
    }

    // Função para converter URI em File
    private fun uriToFile(uri: Uri): File? {
        // Converte a URI para um arquivo
        val contentResolver = applicationContext.contentResolver
        val tempFile = File(cacheDir, "temp_image") // Cria um arquivo temporário
        contentResolver.openInputStream(uri)?.use { inputStream ->
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream) // Copia os dados da URI para o arquivo temporário
            }
        }
        return tempFile // Retorna o arquivo temporário
    }
    // Método para abrir a galeria
    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE) // Adiciona categoria para abrir documentos
            type = "image/*" // Permite todos os tipos de imagem
            putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/jpeg", "image/png")
            ) // Restringe a seleção a JPEG e PNG
        }
        galleryLauncher.launch(intent) // Lança o intent da galeria
    }

    // Método para exibir o nome do arquivo selecionado
    private fun displayFileName(uri: Uri) {
        val fileName = getFileName(uri) // Obtém o nome do arquivo da URI
        if (isImageValid(fileName)) { // Verifica se o formato da imagem é válido
            binding.tieFoto.setText(fileName) // Exibe o nome do arquivo na TextView
        } else {
            Toast.makeText(
                this,
                "Formato de imagem não suportado. Aceitos: JPEG, JPG, PNG.",
                Toast.LENGTH_SHORT
            ).show() // Exibe mensagem de erro se o formato não for válido
        }
    }

    // Método para obter o nome do arquivo da URI
    private fun getFileName(uri: Uri): String {
        var name = ""
        // Consulta o conteúdo da URI para obter o nome do arquivo
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex =
                it.getColumnIndex(OpenableColumns.DISPLAY_NAME) // Obtém o índice da coluna DISPLAY_NAME
            if (nameIndex != -1 && it.moveToFirst()) { // Verifica se a consulta foi bem-sucedida
                name = it.getString(nameIndex) // Obtém o nome do arquivo
            }
        }
        return name // Retorna o nome do arquivo
    }

    // Método para validar se o formato do arquivo é uma imagem suportada
    private fun isImageValid(fileName: String): Boolean {
        // Verifica se o nome do arquivo termina com as extensões válidas
        return fileName.endsWith(".jpg", true) || fileName.endsWith(
            ".jpeg",
            true
        ) || fileName.endsWith(".png", true)
    }

    @RequiresApi(Build.VERSION_CODES.O)



    private fun cadastrarAnimal() {

        // Verificando se a foto foi selecionada (se não, mostramos um erro)
        val fotoAnimal: File? = fotoSelecionada // Aqui você deve garantir que fotoSelecionada é do tipo File?

        // Verifica se a foto não foi fornecida, retornando erro se necessário
        if (fotoSelecionada == null) {
            Toast.makeText(this, "A foto do animal é obrigatória!", Toast.LENGTH_SHORT).show()
            return
        }

        val clienteViewModel: ClienteViewModel by viewModels()

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Obtendo os dados de SharedPreferences para o Cliente
        val nomeUsuario = sharedPreferences.getString("nomeUsuario", "Nome não disponível")
        val email = sharedPreferences.getString("emailUsuario", "Email não disponível")
        val senha = sharedPreferences.getString("senhaUsuario", "Senha não disponível")
        val telefone = sharedPreferences.getString("telefoneUsuario", "Senha não disponível")
        val cpf = sharedPreferences.getString("cpfUsuario", "cpf não disponível")
        val id = sharedPreferences.getLong("idUsuario", 0L)

        // Criando o modelo Cliente
        val cliente = ClienteModel(
            idCliente = id ?: 0L,
            email = email ?: "Email não disponível",
            passwordCliente = senha ?: "Senha não disponível",
            telefone = telefone ?: "Tel",
            cpf = cpf ?: "cpf não disponivel",
            nome = nomeUsuario ?: "nome não disponivel"
        )

        // Animais
        val nome = binding.tieNome.text.toString()
        val idade = binding.ComboIdade.text.toString()
        val raca = binding.ComboRaca.text.toString()
        val cor = binding.ComboCor.text.toString()
        val especie = binding.ComboEspecie.text.toString()
        val sexo = binding.ComboSexo.text.toString()
        val peso = binding.tiePeso.text.toString()
        val descricao = binding.tieDescricao.text.toString()

        // Criando o modelo Animal (sem foto)
        val animal = AnimalModel(
            nome = nome,
            idade = idade,
            raca = raca,
            cor = cor,
            peso = peso,
            especie = especie,
            sexo = sexo,
            descricao = descricao,
            foto = ByteArray(0), // Passa um array vazio, pois a foto será enviada separadamente
            cliente = cliente // Passando o cliente
        )


        // Chama a função do viewModel para cadastrar o animal
        clienteViewModel.cadastrarAnimal(animal , fotoAnimal) { response ->

            if (response.isSuccessful) {
                Log.d("response", "Cadastro realizado com sucesso!")
                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("response", "Erro no Cadastro: ${response.message()}")
                Toast.makeText(this, "Erro no Cadastro!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}