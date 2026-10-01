package br.com.pettopia.viewModel

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import br.com.pettopia.API.ApiService
import androidx.lifecycle.viewModelScope
import br.com.pettopia.API.ApiUtils
import br.com.pettopia.API.ApiUtils.apiService
import br.com.pettopia.API.RetrofitClient
import br.com.pettopia.Enum.TipoDenuncia
import br.com.pettopia.model.AnimalModel
import br.com.pettopia.model.AuthResponse
import br.com.pettopia.model.ClienteModel
import br.com.pettopia.model.Comprovante
import br.com.pettopia.model.DenunciaModel
import br.com.pettopia.model.Documentos
import br.com.pettopia.model.LoginRequest
import br.com.pettopia.model.ServicosModel
import br.com.pettopia.model.ValidarToken
import br.com.pettopia.repository.AdocaoRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class ClienteViewModel : ViewModel() {

    private val _documentosList = MutableLiveData<List<Documentos>>()
    val documentosList: LiveData<List<Documentos>> get() = _documentosList

    private val _comprovanteResponse = MutableLiveData<Documentos?>()
    val comprovanteResponse: MutableLiveData<Documentos?> = _comprovanteResponse

    // LiveData para representar o erro
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _response = MutableLiveData<Response<AuthResponse>>()
    val response: LiveData<Response<AuthResponse>> get() = _response

    private val _denunciaResponse = MutableLiveData<Response<DenunciaModel>>()
    val denunciaResponse: LiveData<Response<DenunciaModel>> get() = _denunciaResponse

    private val _void = MutableLiveData<Response<Void>>()
    val void: LiveData<Response<Void>> get() = _void

    private val _denuncias = MutableLiveData<List<DenunciaModel>>()
    val denuncias: LiveData<List<DenunciaModel>> get() = _denuncias

    private val _denunciaAtualizada = MutableLiveData<DenunciaModel>()
    val denunciaAtualizada: LiveData<DenunciaModel> get() = _denunciaAtualizada

    private val _cliente = MutableLiveData<Response<ClienteModel>>()
    val cliente: LiveData<Response<ClienteModel>> get() = _cliente

    private val _token = MutableLiveData<Response<ValidarToken>>()
    val token: LiveData<Response<ValidarToken>> get() = _token

    // LiveData para armazenar a lista de comprovantes
    private val _comprovantes = MutableLiveData<List<ServicosModel>>()
    val comprovantes: LiveData<List<ServicosModel>> get() = _comprovantes




    private val repository: AdocaoRepository = AdocaoRepository()

    fun cadastrarCliente(cliente: ClienteModel, callback: (Response<AuthResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                // Chama a API para cadastrar o cliente
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                val response = apiService.cadastrarCliente(cliente)
                _response.value = response // Atualiza o LiveData com a resposta
                callback(response) // Chama o callback com a resposta
            } catch (e: Exception) {
                // Em caso de erro, você pode atualizar o LiveData com uma resposta de erro ou tratar o erro conforme necessário
                e.printStackTrace()
                // Aqui você pode atualizar o LiveData ou lidar com o erro
            }
        }
    }

    fun loginCliente(loginRequest: LoginRequest, callback: (Response<AuthResponse>) -> Unit) {
        viewModelScope.launch {
            try {
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                val response = apiService.loginCliente(loginRequest)
                _response.value = response
                callback(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun enviarComprovanteRenda(animalId: Long, fileUri: Uri?, context: Context) {
        // Verifique se o fileUri é nulo antes de tentar obter o arquivo
        val file = fileUri?.let { getFileFromUri(it, context) }

        Log.d("File", "File URI: $fileUri")
        // Se o arquivo for nulo, exiba a mensagem de erro e retorne
        if (file == null) {
            Log.d("File", "File is null")
            _errorMessage.value = "Selecione um arquivo válido"
            return
        }

        // Mostra um indicador de carregamento (opcional)
        // _loading.value = true

        viewModelScope.launch {
            try {
                val idCliente = getIdCliente(context)
                // Chama o repositório para enviar os bytes diretamente
                val response = repository.enviarComprovanteRenda(animalId, file, idCliente)
                Log.d("IdCliente", "Id do cliente: $idCliente")
                Log.d("idAnimal", "Id do animal: $animalId")
                Log.d("File", "File: $file")
                if (response != null) {
                    Log.d("Response", "Response: $response")
                    _comprovanteResponse.value = response // Atualiza a resposta se for bem-sucedida
                } else {
                    Log.d("Response", "Response is null")
                    _errorMessage.value =
                        "Erro ao enviar comprovante" // Trata erro de resposta nula
                }
            } catch (e: Exception) {
                Log.d("Error", "Error: ${e.message}")
                _errorMessage.value = "Erro de conexão ou servidor"
                e.printStackTrace()
            } finally {
                // Ocultar o indicador de carregamento (opcional)
                // _loading.value = false
            }
        }
    }

    fun getIdCliente(context: Context): Long {
        val sharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        Log.d("IdUsuario", "Id do cliente: ${sharedPreferences.getLong("idUsuario", 0L)}")
        return sharedPreferences.getLong("idUsuario", 0L)
    }

    private fun getFileFromUri(uri: Uri, context: Context): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "comprovante_${System.currentTimeMillis()}.pdf")
            Log.d("File", "File: $file")

            inputStream?.use { input ->
                FileOutputStream(file).use { output ->
                    Log.d("File", "FileInput: $file")
                    input.copyTo(output)
                }
            }

            file // Retorna o arquivo gerado
        } catch (e: Exception) {
            Log.d("Error", "Error: ${e.message}")
            e.printStackTrace()
            null // Retorna null se algo deu errado
        }
    }

    fun getDenuncias(idCliente: Long) {
        viewModelScope.launch {
            try {
                // Chama o método suspenso para listar as denúncias
                val response = apiService.listarDenuncia(idCliente)

                Log.e("ViewModel", "Esperando a reposta do servidor.")

                // Verifica se a resposta foi bem-sucedida e obtém o corpo
                if (response.isSuccessful) {
                    // Atualiza o LiveData com os dados recebidos

                    _denuncias.postValue(response.body())
                    Log.e("ViewModel", "Acerto na API: ${response.code()} - ${response.message()}")
                } else {
                    // Em caso de falha, você pode mostrar uma mensagem de erro ou log
                    Log.e("Denuncias", "Erro ao listar denúncias: ${response.message()}")
                    _denuncias.postValue(emptyList()) // Retorna uma lista vazia em caso de erro
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Lidar com erros, como exceções de rede
                _denuncias.postValue(emptyList()) // Retorna lista vazia em caso de erro
                Log.e("ViewModel", "Erro ao carregar denúncias: ${e.message}")
            }
        }
    }
//    fun excluirDenuncia(idDenuncia: Long) {
//        viewModelScope.launch {
//            try {
//                ApiUtils.apiService.excluirDenuncia(idDenuncia) // Chama a API para excluir
//                Log.e("ViewModel", "Denúncia $idDenuncia excluída com sucesso.")
//
//                // Atualiza a lista local de denúncias (remove a denúncia excluída da lista atual)
//
//            } catch (e: Exception) {
//                Log.e("ViewModel", "Erro ao excluir denúncia: ${e.message}")
//            }
//        }
//    }

    fun realizarDenuncia(
        denunciaModel: DenunciaModel,
        callback: (Response<DenunciaModel>) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                val response = apiService.realizarDenuncia(denunciaModel)
                _denunciaResponse.value = response
                callback(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Função para buscar os documentos do cliente
    fun listarDocumentosCliente(idCliente: Long) {
        // Lança uma coroutine no viewModelScope
        viewModelScope.launch {
            try {
                // Chama a função suspend para obter os documentos
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                val documentos = apiService.listarDocumentosCliente(idCliente)
                Log.d("ViewModel", "Documentos: $documentos")
                // Atualiza a lista de documentos no LiveData
                _documentosList.value = documentos
                Log.d("ViewModel", "Documentos carregados com sucesso.")
            } catch (e: Exception) {
                Log.d("ViewModel", "Erro ao carregar documentos: ${e.message}")
                // Trata erros (opcional)
                _documentosList.value =
                    emptyList()  // Você pode definir algum valor de erro ou logar
            }
        }
    }

//    fun editarDenuncia(denunciaModel: DenunciaModel, callback: (Response<DenunciaModel>) -> Unit) {
//        viewModelScope.launch {
//            try {
//                val apiService = RetrofitClient.instance.create(ApiService::class.java)
//                val response = apiService.editarDenuncia(denunciaModel)
//                _denunciaResponse.value = response
//                callback(response)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }
//    fun editarDenuncia(denuncia: DenunciaModel) {
//        viewModelScope.launch {
//            try {
//                // Chama a API para atualizar a denúncia
//                ApiUtils.apiService.editarDenuncia(denuncia.idDenuncia, denuncia)
//
//                // Atualiza a lista local de denúncias (apenas se for necessário)
//                _denuncias.postValue(_denuncias.value?.map {
//                    if (it.idDenuncia == denuncia.idDenuncia) denuncia else it
//                })
//                Log.e("ViewModel", "Denúncia ${denuncia.idDenuncia} atualizada com sucesso.")
//            } catch (e: Exception) {
//                Log.e("ViewModel", "Erro ao editar denúncia: ${e.message}")
//            }
//        }
//    }

    fun editarDenuncia(idDenuncia: Long, tipoDenuncia: String, descricao: String) {
        viewModelScope.launch {
            try {
                // Cria o objeto DenunciaModel com os dados que você deseja atualizar
                val denunciaModel = DenunciaModel(
                    idDenuncia = idDenuncia,
                    tipoDenucias = tipoDenuncia ,
                    descricao = descricao,
                )

                // Chama o método da API para editar a denúncia
                val response = ApiUtils.apiService.editarDenuncia(idDenuncia, denunciaModel)

                if (response.isSuccessful) {
                    // Se a requisição for bem-sucedida, atualiza a variável com a denúncia editada
                    _denunciaAtualizada.postValue(response.body())
                    Log.e("ViewModel", "Denúncia $idDenuncia editada com sucesso.")
                } else {
                    // Se a resposta não for bem-sucedida, loga a falha
                    Log.e("ViewModel", "Erro ao editar a denúncia: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Erro ao editar denúncia: ${e.message}")
            }
        }
    }

    fun deletarDenuncia(idDenuncia: Long) {
        viewModelScope.launch {
            try {
                // Criação do serviço Retrofit
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                Log.e("ViewModel", "Denúncia $idDenuncia excluída com sucesso.")
                val response = apiService.deleteDenuncia(idDenuncia)
                _denuncias.postValue(_denuncias.value?.filter { it.idDenuncia != idDenuncia })
                // Atualiza o LiveData com a resposta


            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("ViewModel", "Erro ao excluir denúncia: ${e.message}")
                // Você pode tratar o erro aqui, por exemplo, definindo um valor de erro no LiveData
            }
        }
    }

    fun cadastrarAnimal(animal: AnimalModel, fotoAnimal: File?,  callback: (Response<AnimalModel>) -> Unit) {
        viewModelScope.launch {
            try {
                // Verificar se a foto foi fornecida
                if (fotoAnimal == null) {
                    callback(Response.error(400, ResponseBody.create(null, "Foto é obrigatória!")))
                    return@launch
                }

                // Criação do serviço Retrofit
                val apiService = RetrofitClient.instance.create(ApiService::class.java)

                // Converter o objeto AnimalModel para JSON e criar o RequestBody
                val gson = Gson()
                val animalJson = gson.toJson(animal)
                val animalRequestBody = animalJson.toRequestBody("application/json".toMediaTypeOrNull())

                // Criar o RequestBody para a foto com tipo MIME correto (por exemplo, "image/*")
                val fotoRequestBody = fotoAnimal.asRequestBody("image/*".toMediaTypeOrNull())

                // Criar o MultipartBody.Part para a foto
                val fotoPart = MultipartBody.Part.createFormData(
                    "fotoAnimal",  // Nome do campo no servidor
                    fotoAnimal.name,  // Nome do arquivo
                    fotoRequestBody  // O corpo do arquivo (RequestBody)
                )

                // Fazer a chamada à API
                val response = apiService.cadastrarAnimal(animalRequestBody, fotoPart)

                // Verificar se a resposta foi bem-sucedida
                if (response.isSuccessful) {
                    // Chamar o callback com sucesso
                    callback(response)
                } else {
                    // Chamar o callback com erro
                    val errorMessage = response.message() ?: "Erro desconhecido"
                    callback(Response.error(400, ResponseBody.create(null, errorMessage)))
                }

            } catch (e: Exception) {
                // Tratar exceção se a requisição falhar
                e.printStackTrace()

                // Retornar erro genérico no callback
                callback(
                    Response.error(
                        500,
                        ResponseBody.create(null, "Erro ao cadastrar o animal.")
                    )
                )
            }
        }
    }



    fun esqueciasenha(cliente: ClienteModel, callback: (Response<ClienteModel>) -> Unit) {

        viewModelScope.launch {
            try {
                Log.d("filha pouta", "filha pouta")
                // Instanciar o serviço de API
                val apiService = RetrofitClient.instance.create(ApiService::class.java)

                // Fazer a chamada à API
                val response = apiService.esqueciasenha(cliente)

                _cliente.value = response
                callback(response)
            } catch (e: Exception) {
                Log.d("Erro", "Erro: ${e.message}")
              e.printStackTrace()
              println("Token enviado com sucesso")
        }
        }
    }
    fun validartoken(token: ValidarToken, callback: (Response<ValidarToken>) -> Unit){

        viewModelScope.launch {

            try {
                Log.d("filha pouta", "filha pouta")
                val ApiService = RetrofitClient.instance.create(ApiService::class.java)

                val response = ApiService.validartoken(token)

                _token.value = response
                callback(response)

            } catch (e: Exception){
                Log.d("Erro", "Erro: ${e.message}")
                e.printStackTrace()
            }
        }
    }
    fun atualizarsenha(cliente: ClienteModel, callback: (Response<ClienteModel>) -> Unit) {

        viewModelScope.launch {
            try {

                // Instanciar o serviço de API
                val apiService = RetrofitClient.instance.create(ApiService::class.java)

                // Fazer a chamada à API
                val response = apiService.atualizarsenha(cliente)

                _cliente.value = response
                callback(response)

            } catch (e: Exception) {
                e.printStackTrace()
                println("Senha Atualizada com Sucesso!")
            }
        }
    }

    fun getComprovantes(idCliente: Long) {
        viewModelScope.launch {
            try {
                // Instância do Retrofit
                val apiService = RetrofitClient.instance.create(ApiService::class.java)

                // Chama a API para obter a lista de comprovantes
                val comprovantesList = apiService.buscarComprovantes(idCliente)

                // Atualiza o LiveData com a lista recebida
                if (comprovantesList.isNullOrEmpty()) {
                    _comprovantes.postValue(emptyList())
                    _errorMessage.postValue("Nenhum comprovante encontrado.")
                } else {

                    // Apenas selecionando os dados que queremos exibir: código e status
                    val dadosComprovantes = comprovantesList.map {
                        Comprovante(it.codigoComprovante.toString(), it.statusPedido.toString())
                    }
                    _comprovantes.postValue(comprovantesList)
                }

            } catch (e: Exception) {
                _comprovantes.postValue(emptyList())
                _errorMessage.postValue("Erro ao carregar comprovantes: ${e.message}")
            }
        }
    }



    fun editarDados(idCliente: Long, nome: String, email: String, telefone: String) {
        viewModelScope.launch {
            try {

                // Cria o objeto ClienteModel com os dados atualizados
                val clienteModel = ClienteModel(
                    idCliente = idCliente,
                    nome = nome,
                    email = email,
                    telefone = telefone
                )

                val apiService = RetrofitClient.instance.create(ApiService::class.java)

                // Chama o método da API para editar os dados do cliente
                val response = ApiUtils.apiService.editarDados(clienteModel , idCliente)

                if (response.isSuccessful) {
                    // Atualiza a variável ou notifica o sucesso
                    Log.d("ViewModel", "Dados do cliente $idCliente atualizados com sucesso.")
                } else {
                    // Loga o erro no caso de resposta não-sucedida
                    Log.e("ViewModel", "Erro ao atualizar dados do cliente: ${response.message()}")
                }
            } catch (e: Exception) {
                // Loga a exceção no caso de erro
                Log.e("ViewModel", "Erro ao atualizar dados do cliente: ${e.message}")
            }
        }
    }

    // Função para baixar o comprovante
    fun baixarComprovante(servicosModel: ServicosModel, context: Context) {
        val base64Comprovante = servicosModel.comprovanteBase64
        if (base64Comprovante != null) {
            // Converte o Base64 para um byte array
            val pdfBytes = Base64.decode(base64Comprovante, Base64.DEFAULT)

            // Se o Android for menor ou igual a 9, pede permissão para gravar no armazenamento externo
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                ) {
                    // Para Android 9 ou inferior, salva diretamente na pasta Downloads
                    try {
                        val fileUri = savePdfToDownloadsLegacy(pdfBytes, context)
                        Toast.makeText(context, "Comprovante baixado com sucesso!", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        Toast.makeText(context, "Falha ao baixar o comprovante.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Solicita permissão de gravação no armazenamento externo
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        1001
                    )
                }
            } else {
                // Para Android 10 e superior, usa MediaStore para salvar no diretório Downloads
                try {
                    val fileUri = savePdfToDownloads(pdfBytes, context)
                    Toast.makeText(context, "Comprovante baixado com sucesso!", Toast.LENGTH_SHORT).show()
                } catch (e: IOException) {
                    Toast.makeText(context, "Falha ao baixar o comprovante.", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Comprovante não encontrado.", Toast.LENGTH_SHORT).show()
        }
    }

    // Função para salvar o PDF usando MediaStore em Android 10 ou superior
    private fun savePdfToDownloads(pdfBytes: ByteArray, context: Context): String {
        // Cria um nome único para o arquivo PDF
        val fileName = "comprovante_${System.currentTimeMillis()}.pdf"

        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)  // Nome do arquivo
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")  // Tipo MIME do PDF
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Documentos/")  // Diretório de Downloads
        }

        // Insere o novo arquivo na coleção de arquivos do MediaStore
        val contentUri = MediaStore.Files.getContentUri("external")
        val fileUri = resolver.insert(contentUri, contentValues)

        if (fileUri != null) {
            // Escreve os bytes do PDF no arquivo
            resolver.openOutputStream(fileUri)?.use { outputStream ->
                outputStream.write(pdfBytes)
                outputStream.close()
            }
        } else {
            throw IOException("Erro ao criar o arquivo no MediaStore.")
        }

        return fileUri.toString()  // Retorna o URI do arquivo criado

    }

    // Função para salvar o PDF na pasta Downloads para versões mais antigas (Android 9 ou inferior)
    private fun savePdfToDownloadsLegacy(pdfBytes: ByteArray, context: Context): String {
        // Cria um nome único para o arquivo PDF
        val fileName = "comprovante_${System.currentTimeMillis()}.pdf"

        // Salva no diretório Downloads diretamente (em Android 9 ou inferior)
        val file = File(context.getExternalFilesDir(null), fileName)

        // Tenta salvar o arquivo no armazenamento externo
        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(pdfBytes)
            outputStream.close()
            return file.absolutePath  // Retorna o caminho do arquivo salvo
        } catch (e: IOException) {
            throw IOException("Erro ao salvar o arquivo: ${e.message}")
        }
    }




}


//LEMBRETRES:
//Um ViewModel é usado para armazenar e gerenciar dados relacionados à interface do usuário de forma que sobreviva a mudanças de configuração, como rotações de tela.
//_response é uma instância de MutableLiveData, que é um tipo de dados que pode ser observado e que pode ser modificado
//Response<AuthResponse> indica que estamos esperando uma resposta que contém um objeto do tipo AuthResponse, que geralmente é a resposta da API de autenticação.
// O uso de um nome começando com um underscore (_response) é uma convenção comum para indicar que essa variável é privada e não deve ser acessada diretamente fora da classe.
//val response: LiveData<Response<AuthResponse>> get() = _response:
//Esta linha expõe uma propriedade somente leitura chamada response. Isso permite que outras classes (como Activities ou Fragments) observem mudanças nesse LiveData, mas não possam modificá-lo diretamente. Isso é importante para manter o encapsulamento.
//fun cadastrarCliente(cliente: ClienteModel, callback: (Response<AuthResponse>) -> Unit):
//Este é um método público que aceita um objeto cliente (do tipo ClienteModel) e um callback (uma função que aceita um Response<AuthResponse> como parâmetro). Esse método é responsável por cadastrar um cliente, chamando a API e gerenciando a resposta.
//viewModelScope é um escopo de coroutine que é associado ao ciclo de vida do ViewModel. O uso de launch inicia uma nova coroutine que permite realizar operações assíncronas, como chamadas de rede, sem bloquear a thread principal da interface do usuário


//Depois de obter a resposta da API, esta linha atualiza o valor de _response, notificando todos os observadores de que a resposta foi recebida.
//o callback passado como argumento é chamado com a response. Isso permite que a classe que chamou cadastrarCliente processe a resposta da API, como redirecionar o usuário ou mostrar uma mensagem.