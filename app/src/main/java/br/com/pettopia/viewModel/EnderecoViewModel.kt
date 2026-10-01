package br.com.pettopia.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.pettopia.model.EnderecoModel
import br.com.pettopia.repository.EnderecoRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class EnderecoViewModel ( private val enderecoRepository: EnderecoRepository):ViewModel(){

    val enderecoLiveData = MutableLiveData<EnderecoModel>()
    val erroLiveData = MutableLiveData<String>()

    fun getEndereco(cep:String){
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = async {
                    enderecoRepository.getEndereco(cep)
                }.await()

                if (response.isSuccessful){
                    enderecoLiveData.value = response.body()
                }else{
                    erroLiveData.value = "não foi possivel retomar o endereço"
                }
            }catch (e:Exception){
                erroLiveData.value = "Erro"
                e.printStackTrace()
            }
        }
    }

    class EnderecoViewModelFactory(
        private val repository: EnderecoRepository
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {

            // Verifica se o modelClass é do tipo EnderecoViewModel
            if (modelClass.isAssignableFrom(EnderecoViewModel::class.java)) {

                // Retorna uma nova instância do EnderecoViewModel, passando o repository
                @Suppress("UNCHECKED_CAST")
                return EnderecoViewModel(repository) as T
            }

            // Caso o ViewModel seja desconhecido, lança uma exceção
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

}