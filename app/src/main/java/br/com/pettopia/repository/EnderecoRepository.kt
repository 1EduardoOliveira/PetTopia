package br.com.pettopia.repository

import br.com.pettopia.model.EnderecoModel
import br.com.pettopia.service.EnderecoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class EnderecoRepository (private val service: EnderecoService) {

    suspend fun getEndereco(cep:String):Response<EnderecoModel>{
        return withContext(Dispatchers.IO){
            service.getEndereco(cep)
        }
    }
}