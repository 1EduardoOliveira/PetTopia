package br.com.pettopia.repository

import android.util.Log
import br.com.pettopia.API.ApiService
import br.com.pettopia.API.RetrofitClient
import br.com.pettopia.model.Documentos
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.io.File

    // Envia o comprovante de renda para o servidor
    class AdocaoRepository {
        private val apiService = RetrofitClient.instance.create(ApiService::class.java)

        // Envia o comprovante de renda para o servidor
        suspend fun enviarComprovanteRenda(
            idAnimal: Long,
            file: File,
            idCliente: Long
        ): Documentos? {
            // Converte o arquivo para MultipartBody.Part
            val requestFile = file.asRequestBody("application/pdf".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", file.name, requestFile)

            // Converte os IDs para RequestBody
            val idAnimalBody = idAnimal.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val idClienteBody = idCliente.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            // Faz a chamada à API
            val response = apiService.enviarDocumentos(
                idCliente = idClienteBody,
                idAnimal = idAnimalBody,
                file = filePart
            )

            // Retorna a resposta se for bem-sucedida, ou null caso contrário
            return if (response.isSuccessful) {
                response.body()
            } else {
                // Log para diagnosticar problemas
                Log.e("AdocaoRepository", "Erro na resposta: ${response.errorBody()?.string()}")
                null
            }
        }
    }
