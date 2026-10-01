package br.com.helios.mobile.api

import br.com.pettopia.service.EnderecoService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ModuloConectividade {

    private const val viaCep = "https://viacep.com.br/"

    // Função para criar o Retrofit e retornar o serviço
    fun getEnderecoService(): EnderecoService {

        // Log de requisições para depuração
        val log = HttpLoggingInterceptor()
        log.level = HttpLoggingInterceptor.Level.BODY

        // Configurações do OkHttpClient
        val client = OkHttpClient
            .Builder()
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request()
                        .newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .build()
                )
            }
            .addInterceptor(log)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // Criação do Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(viaCep) // URL base da API
            .client(client)
            .addConverterFactory(GsonConverterFactory.create()) // Converte JSON para objetos Kotlin
            .build()

        // Retorna a instância do EnderecoService
        return retrofit.create(EnderecoService::class.java)
    }
}