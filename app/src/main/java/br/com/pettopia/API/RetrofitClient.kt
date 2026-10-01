package br.com.pettopia.API

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


//CRIANDO O OBJETO RETROFIT PARA FAZER A CONEXAO COM O SERVIDOR
object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.64:8081" // Altere pelo IP do seu servidor

    //Criando uma instacia lazy para ela ser usada apenas quando necessario
    val instance: Retrofit by lazy {

        //configuracao de requisao
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS) // Tempo limite de conexão
            .readTimeout(30, TimeUnit.SECONDS)    // Tempo limite de leitura
            .writeTimeout(30, TimeUnit.SECONDS)   // Tempo limite de escrita
            .build()

        //Configurando a configuracao do Retrofit
        Retrofit.Builder()
            .baseUrl(BASE_URL) //Passando a Url IP do meu servidor
            .addConverterFactory(GsonConverterFactory.create()) //Passando o conversor GSON para serializar e deserializar JSON
            .build()

    }

}