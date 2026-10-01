package br.com.pettopia.service

import br.com.pettopia.API.RetrofitClient
import br.com.pettopia.model.EnderecoModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface EnderecoService {
    @GET("ws/{cep}/json/")
    suspend fun getEndereco(@Path("cep") cep: String): Response<EnderecoModel>

    companion object {
        private const val BASE_URL = "https://viacep.com.br/"

        fun create(): EnderecoService {
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()

            return retrofit.create(EnderecoService::class.java)
        }
    }
}

