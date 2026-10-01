package br.com.pettopia.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.pettopia.API.ApiService
import br.com.pettopia.API.RetrofitClient
import br.com.pettopia.model.AnimalModel
import kotlinx.coroutines.launch

class AnimalViewModel : ViewModel() {
    private val _animaisParaAdocao = MutableLiveData<List<AnimalModel>>()
    val animaisParaAdocao: LiveData<List<AnimalModel>> get() = _animaisParaAdocao

    fun fetchAnimaisParaAdocao() {
        viewModelScope.launch {
            try {
                val apiService = RetrofitClient.instance.create(ApiService::class.java)
                val response = apiService.getAnimaisParaAdocao()

                Log.d("AnimalViewModel", "Resposta da API: ${response.body()}")

                if (response.isSuccessful) {
                    _animaisParaAdocao.postValue(response.body())

                    Log.d("AnimalViewModel", "Sucesso na requisição: ${response.body()}")
                } else {
                    // Trate erros de API, se necessário
                    _animaisParaAdocao.postValue(emptyList())

                    Log.d("AnimalViewModel", "Erro na requisição: ${response.body()}")
                }
            } catch (e: Exception) {
                // Trate exceções, como falhas de rede
                _animaisParaAdocao.postValue(emptyList())

            }
        }
    }
}