package br.com.pettopia.API


// Criando o objeto ApiUtils, na crio uma instancia para poder me chamar meu retrofit e api servir mais facil e dinamico de se acessar

    object ApiUtils {
        val apiService: ApiService by lazy {
            RetrofitClient.instance.create(ApiService::class.java)
        }

    }
